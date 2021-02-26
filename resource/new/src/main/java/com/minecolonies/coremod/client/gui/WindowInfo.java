package com.minecolonies.coremod.client.gui;

import java.util.Arrays;
import java.util.function.Supplier;


public class WindowInfo extends AbstractWindowSkeleton
{
    /**
     * Link to the xml file of the window.
     */
    private static final String WINDOW_RESOURCE = ":gui/windowinfo.xml";

    /**
     * Constructor for the skeleton class of the windows.
     *
     * @param building The building the info window is for.
     */
    public WindowInfo(final IBuildingView building)
    {
        super(Constants.MOD_ID + WINDOW_RESOURCE);

        registerButton(BUTTON_EXIT, () -> building.openGui(false));

        final String translationPrefix = COM_MINECOLONIES_INFO_PREFIX + building.getSchematicName() + ".";
        final Supplier<TextBuilder> nameBuilder = () -> PaneBuilders.textBuilder().colorName("red");
        final Supplier<TextBuilder> textBuilder = () -> PaneBuilders.textBuilder().colorName("black");
        final Supplier<View> pageBuilder = () -> {
            final View ret = new View();
            ret.setSize(switchView.getWidth(), switchView.getHeight());
            return ret;
        };

        for (int i = 0;; i++)
        {
            if (!I18n.hasKey(translationPrefix + i))
            {
                break;
            }

            final View view = pageBuilder.get();
            switchView.addChild(view);

            final Text name = nameBuilder.get().append(new TranslationTextComponent(translationPrefix + i + ".name")).build();
            name.setPosition(30, 0);
            name.setSize(90, 11);
            name.setTextAlignment(Alignment.MIDDLE);
            name.putInside(view);

            final TextBuilder preText = textBuilder.get();
            Arrays.stream(LanguageHandler.format(translationPrefix + i).split("\\n"))
                .map(StringTextComponent::new)
                .forEach(preText::appendNL);
            final Text text = preText.build();
            text.setPosition(0, 16);
            text.setSize(150, 194);
            text.setTextAlignment(Alignment.TOP_LEFT);
            text.putInside(view);
        }

        setPage(false, 0);
    }
}
