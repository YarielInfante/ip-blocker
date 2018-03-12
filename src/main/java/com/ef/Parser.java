package com.ef;

import com.ef.domain.ParserDTO;
import com.ef.parser.ParserInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Parser {


    public static void main(String... args) {

        try {

            Map<String, String> argsMap = new HashMap<>();

            for (String arg : args) {
                String[] split = arg.split("=");
                argsMap.put(split[0].trim(), split[1].trim());
            }

            String fileUrl = argsMap.get("--accesslog");
            String startDate = argsMap.get("--startDate");
            String duration = argsMap.get("--duration");
            int threshold = Integer.valueOf(argsMap.get("--threshold"));

            ParserDTO parserDTO = new ParserDTO(fileUrl, startDate, duration, threshold);

            ParserInvoker parserInvoker = new ParserInvoker();
            BatchStatus batchStatus = parserInvoker.invoke(parserDTO);

            log.info("Batch status : " + batchStatus);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
