package com.DecupleProject.Contents.RPG;

import com.DecupleProject.Core.ExceptionReport;
import com.DecupleProject.Core.ReadFile;
import com.DecupleProject.Core.Util.EasyEqual;
import com.DecupleProject.Core.WriteFile;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.FileNotFoundException;

public class Inventory {

    private final User user;
    private final File BASE_FILE;

    private final ReadFile r = new ReadFile();
    private final WriteFile w = new WriteFile();

    private final EasyEqual e = new EasyEqual();

    public Inventory(User user) {
        this.user = user;

        File databaseInventoryFile = new File("D:/Database/Inventory/");
        if (!databaseInventoryFile.exists()) databaseInventoryFile.mkdir();

        this.BASE_FILE = new File(databaseInventoryFile.getPath() + "/" + this.user.getId() + ".txt");
    }

    public boolean addItem(String itemCode, int value) {

        try {

            String inventory = null;
            if (BASE_FILE.exists()) inventory = r.readString(BASE_FILE);


            if (inventory != null) {
                if (inventory.contains(itemCode)) {
                    String[] items = inventory.split("[,]");
                    StringBuilder finalItem = new StringBuilder();

                    int i = 0;

                    for (String item : items) {
                        String[] s = item.split("x");

                        if (e.eq(itemCode, s[0])) {
                            int itemValue = Integer.parseInt(s[1]) + value;
                            finalItem.append(itemCode).append("x").append(itemValue);
                        } else {
                            finalItem.append(item);
                        }

                        if (i < items.length) {
                            finalItem.append(",");
                        }

                        i++;
                    }

                    w.writeString(BASE_FILE, finalItem.toString());
                } else {
                    w.writeString(BASE_FILE, inventory + "," + itemCode + "x" + value);
                }
            } else {
                w.writeString(BASE_FILE, itemCode + "x" + value);
            }
            return true;

        } catch (Exception e) {
            new ExceptionReport(e);
            return false;
        }

    }

    public boolean grantItem(String itemCode, int value) {

        try {

            String inventory = null;

            if (BASE_FILE.exists()) inventory = r.readString(BASE_FILE);

            if (inventory != null) {
                if (inventory.contains(itemCode)) {
                    String[] items = inventory.split("[,]");
                    StringBuilder finalItem = new StringBuilder();

                    int i = 0;

                    for (String item : items) {
                        String[] s = item.split("x");

                        if (e.eq(itemCode, s[0])) {
                            int itemValue = Integer.parseInt(s[1]) - value;

                            if (itemValue > 0)
                                finalItem.append(itemCode).append("x").append(itemValue);
                        } else {
                            finalItem.append(item);
                        }

                        if (i < items.length) {
                            finalItem.append(",");
                        }

                        i++;
                    }

                    w.writeString(BASE_FILE, finalItem.toString());
                } else {
                    return false;
                }
            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            new ExceptionReport(e);
            return false;
        }

    }

    public void sendItemInfo() {

        String inventory = r.readString(BASE_FILE);

        if (inventory != null) {
            String[] items = inventory.split("[,]");
            StringBuilder itemInfoMessage = new StringBuilder();

            int LF = 0;

            for (String item : items) {
                String[] itemInfo = item.split("x");
                String itemName = getEmojiByItemCode(itemInfo[0]);
                int itemValue = Integer.parseInt(itemInfo[1]);

                LF++;

                itemInfoMessage.append(itemName).append(" [x").append(itemValue).append("]");

                if (LF == 4) {
                    itemInfoMessage.append("\n");
                } else {
                    itemInfoMessage.append(" | ");
                }

            }

            user.openPrivateChannel().complete().sendMessage(user.getAsMention() + "**님의 인벤토리 정보입니다!**\n\n" +itemInfoMessage.toString()).queue();
        } else {
            user.openPrivateChannel().complete().sendMessage("**인벤토리가 비어 있네요.**").queue();
        }

    }

    public String getEmojiByItemCode(String itemCode) {
        switch (itemCode) {
            case "000":
                return "\uD83C\uDF32 (나무 장작)";
            case "001":
                return "\uD83D\uDC1F (등 푸른 생선)";
            case "002":
                return "\uD83C\uDF3E (농사 수확물)";
            case "003":
                return "\uD83E\uDDEA (하급 기력 포션)";
            case "004":
                return "\uD83C\uDF56 (생고기)";
            default:
                return "\uD83E\uDD14 (Thinking face)";
        }
    }

}
