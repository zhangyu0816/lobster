package com.zb.lib_base.adaptive;

import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;


public class AdaptiveAtrr {
    private AdaptiveAtrr() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取换算比例
     *
     * @param baseIndex 角标 参考标准
     * @return 换算比例
     */
    private static float getConversionRatio(int baseIndex) {
        String base = "";
        switch (baseIndex) {
            case 1:
                base = "width";
                break;
            case 2:
                base = "height";
                break;
            case 3:
                base = "min";
                break;
            case 4:
                base = "max";
                break;
            case 0:
            case 5:
                base = "";
                break;
            default:
                throw new IllegalArgumentException("invalid parameter ===> " + base);
        }
        return AdaptiveBase.getConversionRatio(TextUtils.isEmpty(base) ? AdaptiveBase.DEFAULT : base);
    }

    /**
     * =============================================================================================
     * 支持的适配属性
     * =============================================================================================
     */

    /**
     * 适配 View 的宽高
     *
     * @param view
     * @param baseWidth  宽度参考标准
     * @param baseHeight 高度参考标准
     */
    @BindingAdapter(value = {"fit_base_width", "fit_base_height"}, requireAll = false)
    public static void setLayoutSize(View view, int baseWidth, int baseHeight) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = (int) (lp.width * getConversionRatio(baseWidth));
        lp.height = (int) (lp.height * getConversionRatio(baseHeight));
        view.setLayoutParams(lp);
    }

    /**
     * 适配 View 的最小宽度
     *
     * @param view
     * @param minWidth 最小宽度
     * @param base     最小宽度的参考标准
     */
    @BindingAdapter(value = {"fit_layout_minWidth", "fit_base_minWidth"}, requireAll = false)
    public static void setLayoutMinWidth(View view, int minWidth, int base) {
        if (Build.VERSION.SDK_INT < 16) {
            view.setMinimumWidth((int) (minWidth * getConversionRatio(base)));
        } else {
            if (minWidth <= 0) {
                view.setMinimumWidth((int) (view.getMinimumWidth() * getConversionRatio(base)));
            } else {
                view.setMinimumWidth((int) (minWidth * getConversionRatio(base)));
            }
        }
    }

    /**
     * 适配 View 的最小高度
     *
     * @param view
     * @param minHeight 最小高度
     * @param base      最小高度的参考标准
     */
    @BindingAdapter(value = {"fit_layout_minHeight", "fit_base_minHeight"}, requireAll = false)
    public static void setLayoutMinHeight(View view, int minHeight, int base) {
        if (Build.VERSION.SDK_INT < 16) {
            view.setMinimumHeight((int) (minHeight * getConversionRatio(base)));
        } else {
            if (minHeight <= 0) {
                view.setMinimumHeight((int) (view.getMinimumHeight() * getConversionRatio(base)));
            } else {
                view.setMinimumHeight((int) (minHeight * getConversionRatio(base)));
            }
        }
    }

    /**
     * 适配 View 的外边距
     *
     * @param view
     * @param baseMargin 外边距参考标准（优先级低于单独设置外边距）
     */
    @BindingAdapter("fit_base_margin")
    public static void setLayoutMargin(View view, int baseMargin) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int leftMargin = (int) (lp.leftMargin * getConversionRatio(baseMargin));
        int topMargin = (int) (lp.topMargin * getConversionRatio(baseMargin));
        int rightMargin = (int) (lp.rightMargin * getConversionRatio(baseMargin));
        int bottomMargin = (int) (lp.bottomMargin * getConversionRatio(baseMargin));
        lp.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        view.setLayoutParams(lp);
    }

    /**
     * 适配 View 的内边距
     *
     * @param view
     * @param basePadding 内边距参考标准（优先级低于单独设置内边距）
     */
    @BindingAdapter("fit_base_padding")
    public static void setLayoutPadding(View view, int basePadding) {
        int leftPadding = (int) (view.getPaddingLeft() * getConversionRatio(basePadding));
        int topPadding = (int) (view.getPaddingTop() * getConversionRatio(basePadding));
        int rightPadding = (int) (view.getPaddingRight() * getConversionRatio(basePadding));
        int bottomPadding = (int) (view.getPaddingBottom() * getConversionRatio(basePadding));
        view.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
    }

    /**
     * 适配 TextView 的字体大小
     *
     * @param textView
     * @param base     字体大小的参考标准
     */
    @BindingAdapter("fit_base_textSize")
    public static void setTextViewSize(TextView textView, int base) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (textView.getTextSize() * getConversionRatio(base)));
    }

//    /**
//     * 适配 TextView 的最大宽度
//     *
//     * @param textView
//     * @param maxWidth 最大宽度
//     * @param base     最大宽度的参考标准
//     */
//    @BindingAdapter(value = {"fit_text_maxWidth", "fit_base_textMaxWidth"}, requireAll = false)
//    public static void setTextViewMaxWidth(TextView textView, int maxWidth, int base) {
//        if (Build.VERSION.SDK_INT < 16) {
//            textView.setMaxWidth((int) (maxWidth * getConversionRatio(base)));
//        } else {
//            if (maxWidth <= 0) {
//                textView.setMaxWidth((int) (textView.getMaxWidth() * getConversionRatio(base)));
//            } else
//                textView.setMaxWidth((int) (maxWidth * getConversionRatio(base)));
//        }
//    }
//
//    /**
//     * 适配 TextView 的最大高度
//     *
//     * @param textView
//     * @param maxHeight 最大高度
//     * @param base      最大高度的参考标准
//     */
//    @BindingAdapter(value = {"fit_text_maxHeight", "fit_base_textMaxHeight"}, requireAll = false)
//    public static void setTextViewMaxHeight(TextView textView, int maxHeight, int base) {
//        if (Build.VERSION.SDK_INT < 16) {
//            textView.setMaxHeight((int) (maxHeight * getConversionRatio(base)));
//        } else {
//            if (maxHeight <= 0) {
//                textView.setMaxHeight((int) (textView.getMaxHeight() * getConversionRatio(base)));
//            } else
//                textView.setMaxHeight((int) (maxHeight * getConversionRatio(base)));
//        }
//    }
//
//    /**
//     * 适配 GridView 的 Item 之间的横向间距
//     *
//     * @param gridView
//     * @param space      横向间距
//     * @param baseHspace 横向间距的参考标准
//     */
//    @BindingAdapter(value = {"fit_grid_hspace", "fit_base_gridHspace"}, requireAll = false)
//    public static void setGridViewHSpace(GridView gridView, int space, int baseHspace) {
//        if (Build.VERSION.SDK_INT < 16) {
//            gridView.setHorizontalSpacing((int) (space * getConversionRatio(baseHspace)));
//        } else {
//            if (space <= 0) {
//                gridView.setHorizontalSpacing((int) (gridView.getHorizontalSpacing() * getConversionRatio(baseHspace)));
//            } else {
//                gridView.setHorizontalSpacing((int) (space * getConversionRatio(baseHspace)));
//            }
//        }
//    }
//
//    /**
//     * 适配 GridView 的 Item 之间的纵向间距
//     *
//     * @param gridView
//     * @param space      纵向间距
//     * @param baseVspace 纵向间距的参考标准
//     */
//    @BindingAdapter(value = {"fit_grid_vspace", "fit_base_gridVspace"}, requireAll = false)
//    public static void setGridViewVSpace(MyGridView gridView, int space, int baseVspace) {
//        if (Build.VERSION.SDK_INT < 16) {
//            gridView.setVerticalSpacing((int) (space * getConversionRatio(baseVspace)));
//        } else {
//            if (space <= 0) {
//                gridView.setVerticalSpacing((int) (gridView.getVerticalSpacing() * getConversionRatio(baseVspace)));
//            } else {
//                gridView.setVerticalSpacing((int) (space * getConversionRatio(baseVspace)));
//            }
//        }
//    }
//
//    /**
//     * 适配 GridView 的列宽
//     *
//     * @param gridView
//     * @param columnWidth 列宽
//     * @param base        列宽的参考标准
//     */
//    @BindingAdapter(value = {"fit_grid_colwidth", "fit_base_gridColwidth"}, requireAll = false)
//    public static void setGridColumnWidth(MyGridView gridView, int columnWidth, int base) {
//        if (Build.VERSION.SDK_INT < 16) {
//            gridView.setColumnWidth((int) (columnWidth * getConversionRatio(base)));
//        } else {
//            if (columnWidth <= 0) {
//                gridView.setColumnWidth((int) (gridView.getColumnWidth() * getConversionRatio(base)));
//            } else {
//                gridView.setColumnWidth((int) (columnWidth * getConversionRatio(base)));
//            }
//        }
//    }
}
