package mangolost.webfluxdemo.controller;

import com.google.gson.GsonBuilder;
import mangolost.webfluxdemo.common.CommonResult;
import mangolost.webfluxdemo.entity.Student;
import mangolost.webfluxdemo.service.StudentService;
import mangolost.webfluxdemo.utils.JsonPUtils;
import mangolost.webfluxdemo.utils.MyBeanUtils;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

/**
 *
 */
@RestController
@RequestMapping("api/student")
@Validated
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(final StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * @param id
     * @param commonResult
     * @param callback
     * @return
     */
    @RequestMapping("get")
    public Mono<Object> getById(@Range @RequestParam("id") final int id, CommonResult commonResult, String callback) {
        return studentService.getById(id).flatMap(student -> {
            commonResult.setData(student);
            return Mono.just(JsonPUtils.doJsonP(commonResult, callback));
        }).switchIfEmpty(Mono.just(JsonPUtils.doJsonP(commonResult, callback)));
    }

    /**
     * @param commonResult
     * @param callback
     * @return
     */
    @RequestMapping("getall")
    public Mono<Object> list(CommonResult commonResult, String callback) {
        return studentService.getAll().flatMap(students -> {
            commonResult.setData(students);
            return Mono.just(JsonPUtils.doJsonP(commonResult, callback));
        });
    }

    /**
     * @param student
     * @param commonResult
     * @param callback
     * @return
     */
    @RequestMapping("add")
    public Mono<Object> create(final Student student, CommonResult commonResult, String callback) {
        return studentService.create(student).flatMap(student2 -> {
            commonResult.setData(student2);
            return Mono.just(JsonPUtils.doJsonP(commonResult, callback));
        });
    }

    /**
     *
     * @param id
     * @param updatedParams
     * @param commonResult
     * @param callback
     * @return
     */
    @RequestMapping("update")
    public Mono<Object> update(@Range @RequestParam("id") final Integer id,
                               @RequestParam("updatedParams") String updatedParams,
                               CommonResult commonResult,
                               String callback) {
        Objects.requireNonNull(updatedParams);

        return studentService.getById(id).flatMap(entity -> {
            Student student = new GsonBuilder().serializeNulls()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create()
                    .fromJson(updatedParams, Student.class);

            //把参数转为一个map
            Map map = new GsonBuilder()
                    .serializeNulls()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create()
                    .fromJson(updatedParams, Map.class);
            //以下字段不允许通过update接口更新
            map.remove("id");  //id不能更新
            map.remove("createTime"); //createTime在创建记录时由数据库自动生成
            map.remove("updateTime"); //updateTime在每次更新记录时由数据库自动生成

            //根据map中设定的字段，修改entity中相应字段
            //利用反射，对map中存储的所有字段key，设置entity属性值为student中对应的属性值
            MyBeanUtils.changeProps(entity, student, map, Student.class);

            return studentService.update(id, entity).flatMap(student1 -> {
                commonResult.setData(student1);
                return Mono.just(JsonPUtils.doJsonP(commonResult, callback));
            }); //更新entity并返回更新后的student
        }).switchIfEmpty(Mono.just(JsonPUtils.doJsonP(new CommonResult(404, "您要更新的对象不存在"), callback)));
    }

    /**
     * @param id
     * @param commonResult
     * @param callback
     * @return
     */
    @RequestMapping("delete")
    public Mono<Object> delete(@Range @RequestParam("id") final Integer id, CommonResult commonResult, String callback) {

        return studentService.getById(id).flatMap(entity -> studentService.delete(id).flatMap(num -> {
            if (num == 1) {
                return Mono.just(JsonPUtils.doJsonP(commonResult, callback));
            } else {
                return Mono.just(JsonPUtils.doJsonP(new CommonResult(500, "删除失败"), callback));
            }
        })).switchIfEmpty(Mono.just(JsonPUtils.doJsonP(new CommonResult(404, "您要删除的对象不存在"), callback)));

    }
}
