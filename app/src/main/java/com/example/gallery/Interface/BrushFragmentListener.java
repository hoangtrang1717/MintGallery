package com.example.gallery.Interface;

public interface BrushFragmentListener {
    void onBrushSizeChangedListener(float size);
    void onBrushOpacityChangedListener(int opacity);
    void onBrushColorChangedListener(int color);
    void onBrushStateChangedListener(boolean isErazer);
}
