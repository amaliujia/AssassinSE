package SearchEngine.Assassin.MultiThreadingLib.FuturePattern;

import SearchEngine.Assassin.Operators.QryResult;

/**
 * @author amaliujia
 */
public class RealData implements Data {
    final QryResult result;

    public RealData(QryResult result) {
        this.result = result;
    }

    @Override
    public QryResult getContent() {
        return result;
    }
}
