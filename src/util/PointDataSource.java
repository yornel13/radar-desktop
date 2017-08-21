package util;

import javafx.scene.layout.HBox;
import model.Position;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import java.util.ArrayList;
import java.util.List;

public class PointDataSource implements JRDataSource {

    private final List<HBox> positionList = new ArrayList<>();
    private int indexRow = -1;

    public PointDataSource(List<HBox> positionList) {
        this.positionList.addAll(positionList);
    }

    public void setPositionToReport(List<HBox> positionList) {
        this.positionList.addAll(positionList);
    }

    @Override
    public boolean next() throws JRException {
        return ++indexRow < positionList.size();
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {

        Object valor = null;

        Position position = (Position) positionList.get(indexRow).getUserData();

        if("point".equals(jrField.getName())) {
            valor = position.getControlPosition().getPlaceName();
        } else if("distance".equals(jrField.getName())) {
            valor = "a 3 metros";
        } else if("time".equals(jrField.getName())) {
            valor = RadarDate.getDiaMesConHora(position.getTime());
        }
        return valor;

    }
}
