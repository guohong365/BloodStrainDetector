package com.uc.images;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageFilterManager {
    private static Map<String, ImageFilter> namedFilters =new HashMap<>();
    private static Map<String, List<ImageFilter>> groupedFilters =new HashMap<>();

    public static void register(ImageFilter processor){
        namedFilters.put(processor.getTitle(), processor);
        if(!groupedFilters.containsKey(processor.getCategory())) {
            groupedFilters.put(processor.getCategory(), new ArrayList<>());
        }
        groupedFilters.get(processor.getCategory()).add(processor);
    }

    public static List<ImageFilter> getProcessors(String category){
        return groupedFilters.get(category);
    }

    public static ImageFilter getProcessor(String name){
        return namedFilters.get(name);
    }

    public static FilterRunner apply(String name, Bitmap input, OnProcessCompletedListener completedListener){
        FilterRunner runner=new FilterRunner(getProcessor(name), input, completedListener);
        new Thread(runner).start();
        return runner;
    }
    public interface OnProcessCompletedListener{
        void onCompleted(Bitmap output);
    }

    private static class FilterRunner implements Runnable{
        private boolean completed;
        private boolean failed;
        private ImageFilter filter;
        private Bitmap input;
        private Bitmap result;
        private OnProcessCompletedListener completedListener;

        public FilterRunner(ImageFilter filter, Bitmap input, OnProcessCompletedListener completedListener){
            completed=false;
            failed=false;
            this.filter =filter;
            this.input=input;
            this.completedListener=completedListener;
        }

        public boolean isCompleted() {
            return completed;
        }

        public boolean isFailed() {
            return failed;
        }

        @Override
        public void run(){
            try {
                result = filter.apply(input);
                completed = true;
                if (completedListener != null) {
                    completedListener.onCompleted(result);
                }
            } catch (Exception ex){
                failed=true;
            }
        }
    }
    public static final int CATEGORY_COLOR_FILTERS=0;
    public static final int CATEGORY_PIXEL_FILTERS=1;
    public static final int CATEGORY_GEOMETRY_FILTERS=2;

    public static final int FILTER_GREY_SCALE_ID=0;
    public static final int FILTER_INVERT_ID=1;

}
