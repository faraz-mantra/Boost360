package nowfloats.nfkeyboard.interface_contracts;

/**
 * Created by Admin on 28-02-2018.
 */

public interface CandidateToPresenterInterface extends ItemClickListener {

    void onSpeechResult(String speech);
}
