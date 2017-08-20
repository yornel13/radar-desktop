package util;

import javafx.scene.layout.HBox;
import model.Position;
import model.Watch;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.joda.time.DateTime;
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
        Watch watch = position.getWatch();

        if ("full_name".equals(jrField.getName())) {
           valor = watch.getUser().getLastname()+" "+watch.getUser().getName();
        }
        else if ("dni".equals(jrField.getName())) {
            valor = watch.getUser().getDni();
        }
        else if ("date".equals(jrField.getName())) {
            valor = RadarDate.getFechaConMes(new DateTime(position.getTime()));
        }
        else if ("company".equals(jrField.getName())) {
            valor = watch.getUser().getCompany().getName();
        }
        else if("point".equals(jrField.getName())) {
            valor = position.getControlPosition().getPlaceName();
        }
        else if("time".equals(jrField.getName())) {
            valor = RadarDate.getHora(position.getTime());
        }
        return valor;

    }
}
