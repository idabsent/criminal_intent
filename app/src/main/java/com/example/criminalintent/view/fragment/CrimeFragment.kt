package com.example.criminalintent.view.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.CheckBox
import android.text.TextWatcher
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings.System.DATE_FORMAT
import android.text.Editable
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.ViewModelProvider

import com.example.criminalintent.model.Crime
import com.example.criminalintent.R
import com.example.criminalintent.model.CrimeDetailViewModel
import java.util.*
import android.widget.ImageView
import android.widget.ImageButton
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.criminalintent.filesystem.getFileBitmap
import java.io.File


class CrimeFragment : Fragment(), DateFragment.DateCallbacks {

    private lateinit var titleView: TextView
    private lateinit var dateButton: Button
    private lateinit var inputTitleEditText: EditText
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var chooseSuspectButton: Button
    private lateinit var reportButton: Button
    private lateinit var captureImgView: ImageView
    private lateinit var captureImgButton: ImageButton

    private lateinit var filePath: File
    private lateinit var fileUri: Uri

    private val viewModel by lazy {
        ViewModelProvider(this)[CrimeDetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStop() {
        viewModel.saveCrime(viewModel.crimeLiveData.value)

        super.onStop()
    }

    private fun showImage() {
        if (filePath.exists()) {
            captureImgView.setImageBitmap(getFileBitmap(filePath.path, requireActivity()))
        } else {
            captureImgView.setImageDrawable(null)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleView = view.findViewById(R.id.title)
        dateButton = view.findViewById(R.id.date_button)
        inputTitleEditText = view.findViewById(R.id.input_title)
        solvedCheckBox = view.findViewById(R.id.solved)
        chooseSuspectButton = view.findViewById(R.id.choose_suspect)
        reportButton = view.findViewById(R.id.send_report)
        captureImgView = view.findViewById(R.id.capture_img)
        captureImgButton = view.findViewById(R.id.capture_img_button)

        val id = requireArguments().getSerializable(UUID_KEY) as UUID
        viewModel.loadCrime(id)
        viewModel.crimeLiveData.observe(viewLifecycleOwner) {
            dateButton.text = it!!.date.toString()
            solvedCheckBox.isChecked = it.isSolved
            titleView.text = it.title
            if (it.suspect.isNotBlank()) {
                chooseSuspectButton.text = it.suspect
            }
            filePath = viewModel.getFile(it)
            fileUri = FileProvider.getUriForFile(requireActivity(), "com.example.criminalintent.provider", filePath)
            showImage()
        }

        solvedCheckBox.apply {
            jumpDrawablesToCurrentState()
        }

        dateButton.setOnClickListener {
            val crime = viewModel.crimeLiveData.value
            crime?.let {
                DateFragment.newInstance(crime.date)
                    .show(requireFragmentManager(), "DatePickerDialog")
            }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.crime_report_subject))
            }.also {
                startActivity(Intent.createChooser(it, getString(R.string.send_report)))
            }
        }

        chooseSuspectButton.apply{
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            chooseSuspectButton.setOnClickListener {
                startActivityForResult(intent, CONTACT_REQUEST)
            }

            val packageManager = requireActivity().packageManager
            val resolvedActivity = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null)
                isEnabled = false
        }

        Log.d("CrimeFragment", "filesDir: ${requireContext().filesDir}")

        captureImgButton.apply {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val packageManager = requireActivity().packageManager
            val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveInfo == null) {
                //isEnabled = false
            }

            setOnClickListener {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

                val captureActivities: List<ResolveInfo> = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                for (captureActivity in captureActivities) {
                    requireActivity()
                        .grantUriPermission(
                            captureActivity.activityInfo.packageName,
                            fileUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }

                startActivityForResult(intent, CAPTURE_REQUEST)
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CONTACT_REQUEST && data != null) {
            val uri = data.data
            val requestFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            val cursor = requireActivity().contentResolver.query(uri!!, requestFields, null, null, null)
            cursor?.let {cursor ->
                if (cursor.count == 0) {
                    return
                }

                cursor.moveToFirst()
                val suspect = cursor.getString(0)
                viewModel.crimeLiveData.value?.suspect = suspect
                viewModel.updateCrime(viewModel.crimeLiveData.value)
                chooseSuspectButton.text = suspect
            }

            cursor?.close()

            return
        }

        if (requestCode == CAPTURE_REQUEST && data != null) {
            val uri: Uri? = data.data
            Log.d("CrimeFragment", "capture result: $resultCode")
            showImage()
        }
    }

    override fun dateSelected(date: Date) {
        viewModel.crimeLiveData.value?.date = date
    }

    private fun getCrimeReport() : String {
        val crime = viewModel.crimeLiveData.value!!

        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    override fun onStart() {
        super.onStart()

        solvedCheckBox.setOnCheckedChangeListener { _, isSolved ->
            viewModel.crimeLiveData.value?.isSolved = isSolved
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // unused
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.crimeLiveData.value?.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // unused
            }
        }

        inputTitleEditText.addTextChangedListener(textWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    companion object {
        private const val UUID_KEY = "CrimeId"
        private const val CONTACT_REQUEST = 1
        private const val CAPTURE_REQUEST = 2

        fun newInstance(uuid: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(UUID_KEY, uuid)
            }

            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}

