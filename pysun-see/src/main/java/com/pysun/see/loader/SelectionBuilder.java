package com.pysun.see.loader;

import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectionBuilder {


    private final static String AND = " AND ";
    private final static String OR = " OR ";
    private final static String GROUP_BY = " GROUP BY ";
    private ArrayList<String> mProjection = new ArrayList<>();
    private StringBuilder mSelection = new StringBuilder();
    private ArrayList<String> mSelectionArgs = new ArrayList<>();
    private String mGroupBy = null;
    private String mSortOrder = null;
    private Uri mUri = MediaStore.Files.getContentUri("external");

    public SelectionBuilder() {
        mSelection.append("1 = 1");
    }

    public SelectionBuilder table(Uri uri) {
        this.mUri = uri;
        return this;
    }

    public SelectionBuilder table(Uri uri, String... columns) {
        this.mUri = uri;

        Collections.addAll(this.mProjection, columns);
        return this;
    }

    public SelectionBuilder addColum(String column) {
        this.mProjection.add(column);
        return this;
    }

    public SelectionBuilder where(String... selectionArgs) {
        if (this.mSelectionArgs != null) {
            Collections.addAll(this.mSelectionArgs, selectionArgs);
        }

        return this;
    }

    public SelectionBuilder and(String selection) {
        this.mSelection.append(AND).append(selection);

        return this;
    }

    public SelectionBuilder or(String selection) {
        this.mSelection.append(OR).append(selection);

        return this;
    }

    public SelectionBuilder groupBy(String groupBy) {
        this.mGroupBy = groupBy;
        return this;
    }

    public String getSelection() {

        String groupBy = "";
        if (!TextUtils.isEmpty(mGroupBy)) {
            groupBy = ")" + GROUP_BY + "(" + mGroupBy;
        }
        return mSelection.toString() + groupBy;
    }


    public SelectionBuilder sortOder(String sortOrder) {
        this.mSortOrder = sortOrder;
        return this;
    }

    public String getSortOrder() {
        return mSortOrder;
    }

    public String[] getSelectionArgs() {

        return mSelectionArgs.toArray(new String[mSelectionArgs.size()]);
    }

    public String[] getProjection() {
        return mProjection.toArray(new String[mProjection.size()]);
    }

    public Uri getUri() {
        return mUri;
    }
}
