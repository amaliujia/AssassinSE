package SearchEngine.Assassin.Lucene;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.search.similarities.TFIDFSimilarity;

public class DocLenStoreSimilarity extends SimilarityBase {

    @Override
    protected float score(BasicStats stats, float freq, float docLen) {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }


    /** Encodes the document length in the same way as {@link TFIDFSimilarity}. */
    @Override
    public long computeNorm(FieldInvertState state) {
        final float numTerms;
        if (discountOverlaps)
            numTerms = state.getLength() - state.getNumOverlap();
        else
            numTerms = state.getLength();
        return (long)numTerms;
    }

}
