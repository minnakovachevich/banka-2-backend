package rs.raf.banka2_bek.interbank.model;

/**
 * Status InterbankMessage zapisa.
 *
 * Outbound:
 *  PENDING — poslata, jos nije primljen 200/204; retry-uje se u sledecem
 *            ciklusu InterbankRetryScheduler-a (vidi protokol §2.9). 202
 *            Accepted iz odgovora ostavlja status PENDING.
 *  SENT    — primljen 200/204 — terminalan.
 *  STUCK   — dosegnut MAX_RETRY; potrebna manuelna intervencija (supervisor).
 *
 * Inbound:
 *  INBOUND — primljena, obradjena, odgovor cache-iran (responseBody +
 *            httpStatus); pri retry-u sa istim idempotenceKey-em vraca isto.
 */
public enum InterbankMessageStatus {
    PENDING,
    SENT,
    STUCK,
    INBOUND
}
