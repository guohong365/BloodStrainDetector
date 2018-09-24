package com.uc.images;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ImageModifierFactoryRepository {
    private static ImageModifierFactoryRepository instance;
    private final Map<String, ImageModifierFactory> modifiers;
    private ImageModifierFactoryRepository(){
        modifiers=new HashMap<>();

    }
    private static ImageModifierFactoryRepository getInstance(){
        synchronized (ImageModifierFactoryRepository.class){
            if(instance==null){
                instance=new ImageModifierFactoryRepository();
            }
        }
        return instance;
    }
    private void putFactory(String name, ImageModifierFactory modifier){
        modifiers.put(name, modifier);
    }

    public static ImageModifier get(String name){
        ImageModifierFactory factory= getInstance().getFactory(name);
        if(factory!=null) return factory.create();
        return null;
    }
    public static Collection<String> getNames(){
        return getInstance().modifiers.keySet();
    }

    private ImageModifierFactory getFactory(String key){
        if(modifiers.containsKey(key)){
            return modifiers.get(key);
        }
        return null;
    }

    public static void put(String name, ImageModifierFactory modifier){
        getInstance().putFactory(name, modifier);
    }

}
