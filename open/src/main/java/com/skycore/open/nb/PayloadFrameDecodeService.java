package com.skycore.open.nb;

import com.skycore.common.protocol.rs422.Rs422FrameCodec;
import com.skycore.common.protocol.wb.Wb001PayloadDataRequest;
import com.skycore.common.spo.MhiPilotDictionary;
import com.skycore.common.spo.SpoFrameParser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * WB-001 / NB-001 试点：按 SPO 字段表解帧，可选 RS422 校验。
 */
@Service
public class PayloadFrameDecodeService {

    private final SpoFrameParser parser = new SpoFrameParser();

    public DecodeResult decode(Wb001PayloadDataRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        if (!request.spoDecodeRequested()) {
            return DecodeResult.legacy();
        }

        String frameType = request.getFrameType().trim();
        String rawHex = request.resolveRawHex();
        byte[] frame = SpoFrameParser.hexToBytes(rawHex);

        if (Boolean.TRUE.equals(request.getValidateRs422())) {
            Rs422FrameCodec.validate(frame);
        }

        Map<String, Object> fields = parser.parse(frame, MhiPilotDictionary.fieldsOf(frameType));
        String spoSheet = MhiPilotDictionary.spoSheetOf(frameType);

        Long satTime = request.getSatTime();
        Object timeField = fields.get("SCI200005");
        if (satTime == null && timeField instanceof Number number) {
            satTime = number.longValue();
        }

        String summary = String.format(Locale.ROOT,
                "SPO=%s FRAME=%s FIELDS=%s",
                spoSheet, frameType, fields);

        return new DecodeResult(true, frameType, spoSheet, fields, satTime, summary, frame.length);
    }

    public static final class DecodeResult {
        private final boolean spoDecoded;
        private final String frameType;
        private final String spoSheet;
        private final Map<String, Object> fields;
        private final Long satTime;
        private final String summary;
        private final int frameBytes;

        private DecodeResult(boolean spoDecoded,
                             String frameType,
                             String spoSheet,
                             Map<String, Object> fields,
                             Long satTime,
                             String summary,
                             int frameBytes) {
            this.spoDecoded = spoDecoded;
            this.frameType = frameType;
            this.spoSheet = spoSheet;
            this.fields = fields == null
                    ? Collections.emptyMap()
                    : Collections.unmodifiableMap(new LinkedHashMap<>(fields));
            this.satTime = satTime;
            this.summary = summary;
            this.frameBytes = frameBytes;
        }

        static DecodeResult legacy() {
            return new DecodeResult(false, null, null, Map.of(), null, null, 0);
        }

        public boolean isSpoDecoded() {
            return spoDecoded;
        }

        public String getFrameType() {
            return frameType;
        }

        public String getSpoSheet() {
            return spoSheet;
        }

        public Map<String, Object> getFields() {
            return fields;
        }

        public Long getSatTime() {
            return satTime;
        }

        public String getSummary() {
            return summary;
        }

        public int getFrameBytes() {
            return frameBytes;
        }
    }
}
