package fr.hysekai.tokyo;

import fr.hysekai.uhcapi.game.module.AbstractModule;
import fr.hysekai.uhcapi.game.module.ModuleType;
import fr.hysekai.uhcapi.utils.TagBuilder;

public class TokyoGhoolModule extends AbstractModule {

    public TokyoGhoolModule() {
        super(ModuleType.TOKYO_GHOUL);
    }

    @Override
    public TagBuilder getTag() {
        return new TagBuilder("TokyoGhool");
    }
}
