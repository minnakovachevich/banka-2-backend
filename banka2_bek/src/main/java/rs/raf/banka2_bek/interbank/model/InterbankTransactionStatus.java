package rs.raf.banka2_bek.interbank.model;

/**
 * Statusi InterbankTransaction po protokolu §2.8.
 *
 * Spec ref:
 *   §2.8.4 Local transaction execution (PREPARING → PREPARED → COMMITTED)
 *   §2.8.5 Remote transaction execution (Initiating Bank coordination)
 *   §2.8.7 Local transaction rollback (PREPARED → ROLLED_BACK)
 *   §2.8.8 Remote transaction rollback (Initiating Bank ROLLBACK_TX)
 *   §2.12.1 NEW_TX vote (NO → ROLLED_BACK koordinator strana)
 *
 * Toku transakcije (kao Initiating Bank, IB):
 *   PREPARING — IB je formirao transakciju, prepareLocal prosao, NEW_TX
 *               poruke su u message log-u (jos nisu poslate ili cekaju YES)
 *   PREPARED  — sve banke glasale YES, COMMIT_TX poruke u message log-u
 *   COMMITTED — sve COMMIT_TX poruke su success-ovale (200/204)
 *   ROLLED_BACK — bilo ko glasao NO, ROLLBACK_TX poruke poslate
 *   STUCK     — retry scheduler je odustao posle MAX_RETRY (manuelna
 *               intervencija supervizora)
 *
 * Toku transakcije (kao Recipient Bank, RB):
 *   PREPARED  — primili NEW_TX, glasali YES, sredstva rezervisana
 *   COMMITTED — primili COMMIT_TX, postingi primenjeni
 *   ROLLED_BACK — primili ROLLBACK_TX, rezervacija oslobodjena
 *
 * Cisto lokalna transakcija (single-bank, samo IB ucestvuje, §2.8.4 zadnji
 * paragraf): PREPARED → COMMITTED u jednoj logickoj akciji (ali dve odvojene
 * lokalne transakcije za rezervaciju vs apply).
 */
public enum InterbankTransactionStatus {
    PREPARING,
    PREPARED,
    COMMITTED,
    ROLLED_BACK,
    STUCK
}
