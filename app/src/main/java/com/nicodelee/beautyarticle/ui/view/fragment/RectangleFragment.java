package com.nicodelee.beautyarticle.ui.view.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.florent37.hollyviewpager.HollyViewPagerBus;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.nicodelee.beautyarticle.R;
import com.nicodelee.beautyarticle.app.APP;
import com.nicodelee.beautyarticle.bus.CropEvent;
import com.nicodelee.beautyarticle.bus.TemplateMenuEvent;
import com.nicodelee.beautyarticle.ui.view.activity.CropAct;
import com.nicodelee.beautyarticle.ui.view.activity.FunTemplateAct;
import com.nicodelee.beautyarticle.utils.DevicesUtil;
import com.nicodelee.beautyarticle.utils.TimeUtils;
import com.nicodelee.view.CropImageView;
import java.util.ArrayList;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Nicodelee on 15/9/25.
 */
public class RectangleFragment extends TemplateBase {

  public static final String EXTRA_POSITION = "ARTICLE_POSITION";
  @Bind(R.id.iv_fun) ImageView ivFun;
  @Bind(R.id.tv_month) TextView tvMonth;
  @Bind(R.id.tv_year) TextView tvYear;
  @Bind(R.id.scrollView) ObservableScrollView scrollView;
  @Bind(R.id.card_temp) CardView cardTemp;
  @Bind(R.id.rl_square) RelativeLayout rlSquare;
  @Bind(R.id.ll_root) LinearLayout llRoot;
  private int position;

  public static RectangleFragment newInstance(int sposition) {
    Bundle args = new Bundle();
    args.putInt(EXTRA_POSITION, sposition);
    RectangleFragment fragment = new RectangleFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_rectangle, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    HollyViewPagerBus.registerScrollView(mActivity, scrollView);
    position = getArguments().getInt(EXTRA_POSITION);
    initView();
  }

  private void initView() {
    inflater = LayoutInflater.from(mActivity);
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivFun.getLayoutParams();
    int width = DevicesUtil.screenWidth - DevicesUtil.dip2px(mActivity, 64.f);
    params.width = width;
    params.height = width;
    ivFun.setLayoutParams(params);

    tvMonth.setText(TimeUtils.getEnMonth() + " " + TimeUtils.getSimpleDay());
    tvYear.setText(TimeUtils.getSimpleYear());
  }

  @OnClick({ R.id.iv_fun, R.id.tv_fun }) public void Click(View view) {
    switch (view.getId()) {
      case R.id.tv_fun:
        showEdDialig(true);
        break;
      case R.id.iv_fun:
        showChiocePicDialog();
        break;
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == mActivity.RESULT_OK && requestCode == REQUEST_IMAGE) {
      ArrayList<String> mSelectPath =
          data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
      CropEvent cropEvent = new CropEvent();
      if (position == 1) {
        cropEvent.setCropMode(CropImageView.CropMode.RATIO_3_4);
      } else if (position == 2) {
        cropEvent.setCropMode(CropImageView.CropMode.RATIO_4_3);
      }

      cropEvent.setImagePath(mSelectPath.get(0));
      EventBus.getDefault().postSticky(cropEvent);
      skipIntent(CropAct.class, false);
    }
  }

  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void onEvent(Bitmap corpBitmap) {
    ivFun.setImageBitmap(corpBitmap);
  }

  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void onEvent(Uri uri) {//拍照后编辑
    APP.getInstance().imageLoader.displayImage(uri + "", ivFun, APP.options);
  }

  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
  public void MenuAction(TemplateMenuEvent templateMenuEvent) {
    if (position == templateMenuEvent.getPosition()) {
      new FunTemplateAct().ShareAction(mActivity, templateMenuEvent.getSaveImageAction(), cardTemp);
      removeStickEven();
    }
  }
}
