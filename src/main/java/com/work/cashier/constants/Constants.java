package com.work.cashier.constants;

import com.work.cashier.data_transfert_object.user.UserRoleType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.awt.*;

public class Constants {

    public static StackPane stackPane;
    public static HBox hBox;
    public static VBox containerList;
    public static Object update;
    public static ActionDatabase action = ActionDatabase.INSERT;
    public static UserRoleType userRole;
    private static final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int height = (int)dimension.getHeight();
    private static final int width  = (int)dimension.getWidth();
}
