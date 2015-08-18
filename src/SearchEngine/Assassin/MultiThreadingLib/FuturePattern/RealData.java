package SearchEngine.Assassin.MultiThreadingLib.FuturePattern;

import SearchEngine.Assassin.Operators.QryResult;

/**
 * Created by amaliujia on 15-8-18.
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
