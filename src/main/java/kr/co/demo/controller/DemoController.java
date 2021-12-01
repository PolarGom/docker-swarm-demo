package kr.co.demo.controller;

import kr.co.demo.common.exception.CommonException;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 데모 컨트롤러
 * 
 * @author Demo
 * @since 2021-12-01
 */
@RestController
public class DemoController {

    /**
     * 호스트 이름 가져오기
     * 
     * @return result 호스트 이름
     */
    @GetMapping("/hostname")
    public String getHostName() {

        String result = "";

        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        Process process = null;

        try {

            process = Runtime.getRuntime().exec("hostname");

            inputStreamReader = new InputStreamReader(process.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            result = bufferedReader.readLine();

            process.waitFor();
        } catch ( IOException e ) {

            throw new CommonException(e, "호스트 이름 실행 중 오류가 발생하였습니다.");
        } catch ( InterruptedException e ) {

            throw new CommonException(e, "호스트 이름 실행 완료 대기 중 오류가 발생하였습니다.");
        } finally {

            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(inputStreamReader);

            if ( process != null ) {

                process.destroy();
            }
        }


        return result;
    }
}
