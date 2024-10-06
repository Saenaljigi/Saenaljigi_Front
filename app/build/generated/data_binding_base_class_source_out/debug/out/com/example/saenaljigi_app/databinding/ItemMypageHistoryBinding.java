// Generated by view binder compiler. Do not edit!
package com.example.saenaljigi_app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.saenaljigi_app.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemMypageHistoryBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageView mypageHistoryBox;

  @NonNull
  public final TextView tvDate;

  @NonNull
  public final TextView tvPoint;

  @NonNull
  public final TextView tvTitle;

  @NonNull
  public final TextView tvTotal;

  private ItemMypageHistoryBinding(@NonNull ConstraintLayout rootView,
      @NonNull ImageView mypageHistoryBox, @NonNull TextView tvDate, @NonNull TextView tvPoint,
      @NonNull TextView tvTitle, @NonNull TextView tvTotal) {
    this.rootView = rootView;
    this.mypageHistoryBox = mypageHistoryBox;
    this.tvDate = tvDate;
    this.tvPoint = tvPoint;
    this.tvTitle = tvTitle;
    this.tvTotal = tvTotal;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemMypageHistoryBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemMypageHistoryBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_mypage_history, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemMypageHistoryBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.mypage_history_box;
      ImageView mypageHistoryBox = ViewBindings.findChildViewById(rootView, id);
      if (mypageHistoryBox == null) {
        break missingId;
      }

      id = R.id.tv_date;
      TextView tvDate = ViewBindings.findChildViewById(rootView, id);
      if (tvDate == null) {
        break missingId;
      }

      id = R.id.tv_point;
      TextView tvPoint = ViewBindings.findChildViewById(rootView, id);
      if (tvPoint == null) {
        break missingId;
      }

      id = R.id.tv_title;
      TextView tvTitle = ViewBindings.findChildViewById(rootView, id);
      if (tvTitle == null) {
        break missingId;
      }

      id = R.id.tv_total;
      TextView tvTotal = ViewBindings.findChildViewById(rootView, id);
      if (tvTotal == null) {
        break missingId;
      }

      return new ItemMypageHistoryBinding((ConstraintLayout) rootView, mypageHistoryBox, tvDate,
          tvPoint, tvTitle, tvTotal);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
