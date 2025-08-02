package com.example.invmgnt.invmgnt.Util;

import org.springframework.ui.Model;

public class ExceptionUtil {
    public static String constructExpMsg(Throwable loc, String msg){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String message = msg;
        message+= "<br><br>";

        String location;
        if (stackTrace.length > 2) {
            StackTraceElement elem = stackTrace[2];
            location = String.format("%s.%s(%s:%d)",
                    elem.getClassName(),
                    elem.getMethodName(),
                    elem.getFileName(),
                    elem.getLineNumber());
        }else {
            location = "Unknown location";
        }
        message += location;
        return  message;
    }

    public static String returnErrPage(Model model, Exception e){
        model.addAttribute("errorMessage",e.getMessage());
        return "Error";
    }

}
