package com.workintech.course.controller;

import com.workintech.course.entity.Course;
import com.workintech.course.entity.CourseGpa;
import com.workintech.course.exceptions.CourseException;
import com.workintech.course.exceptions.CourseValidation;
import com.workintech.course.mapping.CourseResponse;
import com.workintech.course.mapping.CourseResponseFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private List<Course> courses;
    //dependency injection aşağıdakiler, constructor oluşturmamız lazım
    private CourseGpa lowGpa;
    private CourseGpa midGpa;
    private CourseGpa highGpa;

    //dependecy injection görevini springe atmak için autowired dememiz lazım(yani anotate etcez), ayrıca o sınınıflara gidip @Component dememiz lazımki springin haberi olsun o sınıflardan, ayrıca qualifier eklememiz lazım, tek sınıf olsa qualifer gerekmezdi şimdi 3 sınıf var
    @Autowired
    public CourseController(@Qualifier("lowCourseGpa") CourseGpa lowGpa,
                            @Qualifier("mediumCourseGpa") CourseGpa midGpa,
                            @Qualifier("highCourseGpa") CourseGpa highGpa) {
        this.lowGpa = lowGpa;
        this.midGpa = midGpa;
        this.highGpa = highGpa;
    }

    @PostConstruct
    public void init() {
        courses = new ArrayList<>();
    }

    @GetMapping
    public List<Course> get() {
        return courses;
    }

    @GetMapping("/{name}")
    public Course getByName(@PathVariable String name) {//aynı isimde bir çok eleman varsa bu metodu List dönebilirdik yukardaki gibi
        List<Course> foundCourses = courses.stream().filter(course -> course.getName().equals(name)).collect(Collectors.toList());
        if (foundCourses.size() == 0) {
            throw new CourseException("Course with given name is not exist. "+name, HttpStatus.BAD_REQUEST);
        }
        return foundCourses.get(0);//liste halinde dön zaten isim bir tane olduğu için sıfırıncı index getirir
        //courses.stream().findFirst() bunu da kullanabilirdik
    }

    @PostMapping("/")
    public CourseResponse save(@RequestBody Course course) {
        CourseValidation.isIdValid(course.getId());
        CourseValidation.checkCoursesValid(course);
        CourseValidation.isDuplicateNameFound(courses, course.getName());
        courses.add(course);
        return CourseResponseFactory.createCourseResponse(course,lowGpa,midGpa,highGpa);



    }

    @PutMapping("/{id}")
    public Course update(@RequestBody Course course, @PathVariable int id) {
        CourseValidation.checkCoursesValid(course);
        Optional<Course> foundCourse = courses.stream().filter(course1 -> course1.getId() == id).findFirst();//gelebilir de gelemeyebilir de (null olabilir) durumunda Optional kullanılabilir
        if (foundCourse.isPresent()) {//List'de id birşey ifade etmez, indexe bakar
            int index = courses.indexOf(foundCourse.get());//Lombok zaten equalsı kendi içinde ezdi, direkt eşitliğe bakabiliyoruz indexOfda
            course.setId(id);
            courses.set(index, course);
            return course;

        } else {
            throw new CourseException("Course with given id is not exist "+id,HttpStatus.BAD_REQUEST);//bunun için ayrı mesajda yazabilirdik, uğraşmadık buraya yazdık
        }


    }
    @DeleteMapping("/{id}")
    public Course delete(@PathVariable int id){
        Optional<Course> foundCourse = courses.stream().filter(course1 -> course1.getId() == id).findFirst();
        if (foundCourse.isPresent()){
            int index = courses.indexOf(foundCourse.get());//get() ile Course tipine getiriyoruz
            courses.remove(index);
            return  foundCourse.get();//optional yazdığımız için hata verdi, .get() yazdık

        }else{
            throw new CourseException("Course with given id is not exist "+id,HttpStatus.BAD_REQUEST);
        }
    }
}
