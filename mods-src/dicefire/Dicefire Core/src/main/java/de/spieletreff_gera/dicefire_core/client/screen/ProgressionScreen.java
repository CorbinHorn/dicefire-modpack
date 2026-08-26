package de.spieletreff_gera.dicefire_core.client.screen;

import de.spieletreff_gera.dicefire_core.config.ProgressionConfig;
import de.spieletreff_gera.dicefire_core.network.UnlockModPayload;
import de.spieletreff_gera.dicefire_core.progression.ModCategory;
import de.spieletreff_gera.dicefire_core.progression.ModUnlock;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import de.spieletreff_gera.dicefire_core.client.ClientProgressionData;
import de.spieletreff_gera.dicefire_core.network.RequestProgressionDataPayload;

public class ProgressionScreen extends Screen {

    private ModCategory selectedCategory;

    public ProgressionScreen() {
        super(Component.literal("Dicefire Progression"));
    }

    @Override
    protected void init() {
        super.init();

        PacketDistributor.sendToServer(
                new RequestProgressionDataPayload()
        );

        if (selectedCategory == null) {
            buildCategoryView();
        } else {
            buildModView();
        }
    }

    private void buildCategoryView() {

        int buttonWidth = 160;
        int buttonHeight = 20;

        int x = (this.width - buttonWidth) / 2;
        int y = 60;

        for (ModCategory category :
                ProgressionConfig.getCategories()) {

            this.addRenderableWidget(
                    Button.builder(
                                    Component.literal(category.displayName()),
                                    button -> {
                                        selectedCategory = category;
                                        rebuildWidgets();
                                    }
                            )
                            .bounds(
                                    x,
                                    y,
                                    buttonWidth,
                                    buttonHeight
                            )
                            .build()
            );

            y += 25;
        }
    }

    private void buildModView() {

        int buttonWidth = 220;
        int buttonHeight = 20;

        int x = (this.width - buttonWidth) / 2;
        int y = 60;

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("< Zurück"),
                                button -> {
                                    selectedCategory = null;
                                    rebuildWidgets();
                                }
                        )
                        .bounds(
                                x,
                                30,
                                80,
                                buttonHeight
                        )
                        .build()
        );

        for (ModUnlock unlock :
                selectedCategory.unlocks()) {

            boolean unlocked =
                    ClientProgressionData.isUnlocked(
                            unlock.id()
                    );

            if (unlocked) {

                this.addRenderableWidget(
                        Button.builder(
                                        Component.literal(
                                                "✓ " + unlock.displayName()
                                        ),
                                        button -> {
                                        }
                                )
                                .bounds(
                                        x,
                                        y,
                                        buttonWidth,
                                        buttonHeight
                                )
                                .build()
                );

            } else {

                long cost = calculateClientCost(
                        selectedCategory
                );

                this.addRenderableWidget(
                        Button.builder(
                                        Component.literal(
                                                unlock.displayName()
                                                        + " - "
                                                        + cost
                                                        + " Ruhm"
                                        ),
                                        button -> {

                                            PacketDistributor.sendToServer(
                                                    new UnlockModPayload(
                                                            unlock.id()
                                                    )
                                            );
                                        }
                                )
                                .bounds(
                                        x,
                                        y,
                                        buttonWidth,
                                        buttonHeight
                                )
                                .build()
                );
            }

            y += 25;
        }
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        graphics.drawString(
                this.font,
                Component.literal(
                        "Ruhm: " + ClientProgressionData.getFame()
                ),
                10,
                10,
                0xFFFFFF
        );

        this.renderBackground(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        graphics.drawCenteredString(
                this.font,
                this.title,
                this.width / 2,
                15,
                0xFFFFFF
        );

        if (selectedCategory != null) {

            graphics.drawCenteredString(
                    this.font,
                    Component.literal(
                            selectedCategory.displayName()
                    ),
                    this.width / 2,
                    45,
                    0xFFFFFF
            );
        }

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private long calculateClientCost(
            ModCategory category
    ) {

        int unlockedCount = 0;

        for (ModUnlock unlock :
                category.unlocks()) {

            if (ClientProgressionData.isUnlocked(
                    unlock.id()
            )) {
                unlockedCount++;
            }
        }

        return 1L + unlockedCount * 2L;
    }

    public void refresh() {
        rebuildWidgets();
    }
}