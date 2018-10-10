package com.uc.drawing;

//绘制样式
public interface TextAppearance extends Appearance{
    float getFontSize();///字体大小
    void setFontSize(float fontSize);
    int getFontStyle(); ///字体风格   位或  Bold=1 Italic=2 Underline=4 Strikeout=4
    void setFontStyle(int fontStyle);
    int getTextAlignment();///文字对齐方式
    void setTextAlignment(int textAlignment);
    int getLineAlignment();///文字行对齐方式
    void setLineAlignment(int lineAlignment);
    String getFontName(); ///字体名称
    void setFontName(String fontName);
    boolean isVerticalText(); ///文字竖排
    void setVerticalText(boolean verticalText);
}
