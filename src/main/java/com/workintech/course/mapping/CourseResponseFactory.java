package com.workintech.course.mapping;

import com.workintech.course.entity.*;

public class CourseResponseFactory {// yeni course oluşturmak için factory metot oluşturduk
    public static CourseResponse createCourseResponse(Course course, CourseGpa low,CourseGpa medium,CourseGpa high){
    if(course.getCredit()<=2){
        return new CourseResponse(course,course.getCredit()*course.getGrade().getCoefficient()* ((LowCourseGpa)low).getGpa());
    }
        if(course.getCredit()==3){
        return new CourseResponse(course,course.getCredit()*course.getGrade().getCoefficient()* ((MediumCourseGpa)medium).getGpa());
    }
        if(course.getCredit()==4){
        return new CourseResponse(course,course.getCredit()*course.getGrade().getCoefficient()* ((HighCourseGpa)high).getGpa());
    }
        return null;
    }
}
