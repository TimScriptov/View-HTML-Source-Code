package com.mcal.codeviewer;

import androidx.annotation.NonNull;

import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;

public class FilteredFilePickerFragment extends FilePickerFragment {
    private static final String EXT1 = ".html";
    private static final String EXT2 = ".css";
    private static final String EXT3 = ".htm";

    private String getExtension(@NonNull File file) {
        String path = file.getPath();
        int lastIndexOf = path.lastIndexOf(".");
        return lastIndexOf < 0 ? null : path.substring(lastIndexOf);
    }

    private File getBackTop() {
        return getArguments().containsKey(AbstractFilePickerFragment.KEY_START_PATH) ? getPath(getArguments().getString(AbstractFilePickerFragment.KEY_START_PATH)) : new File("/");
    }

    public void goUp() {
        mCurrentPath = getParent(mCurrentPath);
        mCheckedItems.clear();
        mCheckedVisibleViewHolders.clear();
        refresh();
    }

    public boolean isBackTop() {
        return compareFiles(mCurrentPath, getBackTop()) == 0 || compareFiles(mCurrentPath, new File("/")) == 0;
    }

    protected boolean isItemVisible(File file) {
        boolean isItemVisible = super.isItemVisible(file);
        if (!isItemVisible || isDir(file) || (mode != 0 && mode != 2)) {
            return isItemVisible;
        }
        String extension = getExtension(file);
        return (EXT1.equalsIgnoreCase(extension) || EXT2.equalsIgnoreCase(extension) || EXT3.equalsIgnoreCase(extension));
    }
}