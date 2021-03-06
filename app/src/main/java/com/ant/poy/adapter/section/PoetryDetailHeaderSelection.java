package com.ant.poy.adapter.section;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ant.poy.R;
import com.ant.poy.entity.PoetryDetail;
import com.ant.poy.utils.CommonUtil;
import com.ant.poy.utils.SharedPreference.PoetryPreference;
import com.ant.poy.utils.ToastUtils;
import com.ant.poy.widget.CustomHtmlTagHandler;
import com.ant.poy.widget.sectioned.StatelessSection;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnowDragon2015
 * <p>
 * 2017/10/18
 * <p>
 * Github ：https://github.com/SnowDragon2015
 */
public class PoetryDetailHeaderSelection extends StatelessSection {

    private Context mContext;
    private PoetryDetail.Poetry poetry;

    private Map<String, String> selectedMap = new HashMap<String, String>();

    private String selectedStr;

    public PoetryDetailHeaderSelection(Context context, PoetryDetail.Poetry poetry, String str) {
        super(R.layout.item_selection_proety_detail_header);

        this.mContext = context;
        this.poetry = poetry;
        this.selectedStr = str;
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new TopItemPoetryDetailHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        TopItemPoetryDetailHolder viewHolder = (TopItemPoetryDetailHolder) holder;

        viewHolder.nameStrTv.setText(poetry.getNameStr());

        /** 设置行间距*/
        viewHolder.contTv.setLineSpacing(CommonUtil.dpToPx(8),1);

        Spanned spanned = Html.fromHtml(poetry.getCont());

        if (null == CommonUtil.getSpannable(spanned.toString(),selectedStr,mContext.getResources().getColor(R.color.tag_selected_color))){
            viewHolder.contTv.setText(Html.fromHtml(poetry.getCont()).toString());
        }else viewHolder.contTv.setText(CommonUtil.getSpannable(spanned.toString(),selectedStr,mContext.getResources().getColor(R.color.tag_selected_color)));


        viewHolder.contTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, PoetryPreference.getInstence().getFontSize());
        viewHolder.shangTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, PoetryPreference.getInstence().getFontSize());


        /** 给cont添加文字复制功能*/
        setSelectableTextHelper(viewHolder.contTv);


        viewHolder.authorTv.setText(poetry.getAuthor());
        viewHolder.dynastyTv.setText(poetry.getChaodai());

        viewHolder.referenceDataTv.setVisibility(View.GONE);

        /** 复制内容到剪切板*/
        viewHolder.copyCkb.setOnClickListener(v -> {
            CommonUtil.copyToClip(viewHolder.contTv.getText().toString());
            ToastUtils.showToast("拷贝《" + poetry.getNameStr() + "》成功");
        });

        /** 译*/

        if (TextUtils.isEmpty(poetry.getYi())) viewHolder.translateCkb.setVisibility(View.GONE);
        else viewHolder.translateCkb.setVisibility(View.VISIBLE);

        if ("true".equals(selectedMap.get(position + "yi")))
            viewHolder.translateCkb.setChecked(true);
        else viewHolder.translateCkb.setChecked(false);


        /** 注*/

        if (TextUtils.isEmpty(poetry.getZhu())) viewHolder.annotationCkb.setVisibility(View.GONE);
        else viewHolder.annotationCkb.setVisibility(View.VISIBLE);

        if ("true".equals(selectedMap.get(position + "zhu")))
            viewHolder.annotationCkb.setChecked(true);
        else viewHolder.annotationCkb.setChecked(false);


        /** 赏*/
        // CheckBox shang_box = holder.getView(R.id.appreciate);

        if (TextUtils.isEmpty(poetry.getShang())) viewHolder.appreciateCkb.setVisibility(View.GONE);
        else viewHolder.appreciateCkb.setVisibility(View.VISIBLE);

        if ("true".equals(selectedMap.get(position + "shang"))) {
            viewHolder.appreciateCkb.setChecked(true);
            viewHolder.shangLineTv.setVisibility(View.VISIBLE);
            viewHolder.shangTv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.appreciateCkb.setChecked(false);
            viewHolder.shangLineTv.setVisibility(View.GONE);
            viewHolder.shangTv.setVisibility(View.GONE);
        }

        /** 设置行间距*/
        viewHolder.shangTv.setLineSpacing(CommonUtil.dpToPx(8),1);


        viewHolder.translateCkb.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                selectedMap.put(position + "yi", "true");
            } else selectedMap.remove(position + "yi");

            changeCont(viewHolder.translateCkb, viewHolder.annotationCkb, viewHolder.appreciateCkb,
                    viewHolder.contTv, poetry, viewHolder.shangTv, viewHolder.shangLineTv);
        }));

        viewHolder.annotationCkb.setOnCheckedChangeListener(((buttonView, isChecked) -> {

            if (isChecked) {
                selectedMap.put(position + "zhu", "true");
            } else selectedMap.remove(position + "zhu");

            changeCont(viewHolder.translateCkb, viewHolder.annotationCkb, viewHolder.appreciateCkb,
                    viewHolder.contTv, poetry, viewHolder.shangTv, viewHolder.shangLineTv);
        }));

        viewHolder.appreciateCkb.setOnCheckedChangeListener(((buttonView, isChecked) -> {

            if (isChecked) {
                selectedMap.put(position + "shang", "true");
                viewHolder.shangTv.setVisibility(View.VISIBLE);
                viewHolder.shangLineTv.setVisibility(View.VISIBLE);
            } else {
                selectedMap.remove(position + "shang");
                viewHolder.shangTv.setVisibility(View.GONE);
                viewHolder.shangLineTv.setVisibility(View.GONE);
            }

            changeCont(viewHolder.translateCkb, viewHolder.annotationCkb, viewHolder.appreciateCkb,
                    viewHolder.contTv, poetry, viewHolder.shangTv, viewHolder.shangLineTv);
        }));

        /** 诗词标签*/
        if (!TextUtils.isEmpty(poetry.getTag())) {
            viewHolder.line1Tv.setVisibility(View.VISIBLE);
            viewHolder.tagTv.setVisibility(View.VISIBLE);
            viewHolder.tagTv.setText(poetry.getTag().toString().replace("|", "，"));
        } else {
            viewHolder.line1Tv.setVisibility(View.GONE);
            viewHolder.tagTv.setVisibility(View.GONE);
        }

        viewHolder.likeCkb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.likeCkb.isChecked()) {

                    ToastUtils.showToast("收藏成功");

                } else ToastUtils.showToast("取消收藏成功");

            }

        });
        viewHolder.praiseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast("赞 +1");
            }
        });

    }

    private void changeCont(CheckBox yi_box, CheckBox zhu_box, CheckBox shang_box, TextView textView, PoetryDetail.Poetry shiWen, TextView shang, TextView line) {

        if (!yi_box.isChecked() && !zhu_box.isChecked() && !shang_box.isChecked()) {
            textView.setText(Html.fromHtml(shiWen.getCont()));

        } else if (yi_box.isChecked() && !zhu_box.isChecked() && !shang_box.isChecked()) {
            textView.setText(Html.fromHtml(shiWen.getYi(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));

        } else if (yi_box.isChecked() && zhu_box.isChecked() && !shang_box.isChecked()) {
            textView.setText(Html.fromHtml(shiWen.getYizhu(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));

        } else if (yi_box.isChecked() && !zhu_box.isChecked() && shang_box.isChecked()) {
            textView.setText(Html.fromHtml(shiWen.getYi(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));
            shang.setText(Html.fromHtml(shiWen.getYishang().substring(shiWen.getYi().length()), null,
                    new CustomHtmlTagHandler(mContext, textView.getTextColors())));

        } else if (yi_box.isChecked() && zhu_box.isChecked() && shang_box.isChecked()) {
            textView.setText(Html.fromHtml(shiWen.getYizhu(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));
            shang.setText(Html.fromHtml(shiWen.getYizhushang().substring(shiWen.getYizhu().length()), null,
                    new CustomHtmlTagHandler(mContext, textView.getTextColors())));

        } else if (!yi_box.isChecked() && zhu_box.isChecked() && !shang_box.isChecked()) {
            textView.setText(Html.fromHtml(shiWen.getZhu(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));

        } else if (!yi_box.isChecked() && zhu_box.isChecked() && shang_box.isChecked()) {

            textView.setText(Html.fromHtml(shiWen.getZhu(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));
            shang.setText(Html.fromHtml(shiWen.getZhushang().substring(shiWen.getZhu().length()), null,
                    new CustomHtmlTagHandler(mContext, textView.getTextColors())));

        } else if (!yi_box.isChecked() && !zhu_box.isChecked() && shang_box.isChecked()) {

            textView.setText(Html.fromHtml(shiWen.getCont(), null, new CustomHtmlTagHandler(mContext, textView.getTextColors())));
            shang.setText(Html.fromHtml(shiWen.getShang().substring(shiWen.getCont().length()), null,
                    new CustomHtmlTagHandler(mContext, textView.getTextColors())));
        }


    }

    static class TopItemPoetryDetailHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.nameStr)
        TextView nameStrTv;

        @BindView(R.id.cont)
        TextView contTv;

        @BindView(R.id.shang_line)
        TextView shangLineTv;

        @BindView(R.id.shang)
        TextView shangTv;

        @BindView(R.id.author)
        TextView authorTv;

        @BindView(R.id.dynasty)
        TextView dynastyTv;

        @BindView(R.id.reference_data)
        TextView referenceDataTv;

        @BindView(R.id.copy)
        CheckBox copyCkb;

        @BindView(R.id.like)
        CheckBox likeCkb;

        @BindView(R.id.praise)
        TextView praiseTv;

        @BindView(R.id.translate)
        CheckBox translateCkb;

        @BindView(R.id.appreciate)
        CheckBox appreciateCkb;

        @BindView(R.id.annotation)
        CheckBox annotationCkb;

        @BindView(R.id.tag)
        TextView tagTv;

        @BindView(R.id.line1)
        TextView line1Tv;

        public TopItemPoetryDetailHolder(View poetryView) {
            super(poetryView);
            ButterKnife.bind(this, poetryView);
        }
    }
}
