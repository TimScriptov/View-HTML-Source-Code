package com.mcal.codeviewer;

import android.os.Environment;

import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;

public class FilePicker extends AbstractFilePickerActivity {
    private FilteredFilePickerFragment filteredFilePickerFragment;

    protected AbstractFilePickerFragment getFragment(String str, int i, boolean z, boolean z2) {
        filteredFilePickerFragment = new FilteredFilePickerFragment();
        FilteredFilePickerFragment filteredFilePickerFragment = this.filteredFilePickerFragment;
        if (str == null) {
            str = Environment.getExternalStorageDirectory().getPath();
        }
        filteredFilePickerFragment.setArgs(str, i, z, z2);
        return filteredFilePickerFragment;
    }

    public void onBackPressed() {
        if (filteredFilePickerFragment == null) {
            super.onBackPressed();
        } else if (filteredFilePickerFragment.isBackTop()) {
            super.onBackPressed();
        } else {
            filteredFilePickerFragment.goUp();
        }
    }
}
