package playground;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

class PropertyTest {

    private DoubleProperty radius = new SimpleDoubleProperty();
    private DoubleProperty area = new SimpleDoubleProperty();

    public PropertyTest(DoubleProperty radius) {
        this.radius = radius;
        setRadius(20);

        // Add listener
        radius.addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
        });

        // Add binding
        area.bind(radius.multiply(2 * Math.PI));

    }

    // Setter
    public final void setRadius(double x) {
        radius.setValue(x);
    }

}
