package com.kesizo.cetpe.backend.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "learning_process")
public class LearningProcess {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "seq_learning_process_generator")
    @SequenceGenerator(name="seq_learning_process_generator",
            sequenceName = "learning_process_seq",
            initialValue=1,
            allocationSize=100)
    private long id;

    @Column(name = "name", nullable = false, length = 256) // field length in the database 256
    @Size(min = 3, max = 256) // it requires to have at least 3 characters
    private String name;

    @Column(name = "description", nullable = false, length = 1024)
    @Size(min = 1, max = 1024)
    private String description;

    @Column(name = "starting_date_time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime starting_date_time;

    @Column(name = "end_date_time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime end_date_time;

    @Column(name = "is_cal1_available", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean is_cal1_available;

    @Column(name = "is_cal2_available", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean is_cal2_available;

    @Column(name = "is_cal3_available", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean is_cal3_available;

    @Column(name = "is_calF_available", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean is_calF_available;

    @Column(name="limit_cal1", nullable = false, precision=2, scale=2)
    private Float limit_cal1;

    @Column(name="limit_cal2", nullable = false, precision=2, scale=2)
    private Float limit_cal2;

    @Column(name="limit_rev1", nullable = false, precision=2, scale=2)
    private Float limit_rev1;

    @Column(name="limit_rev2", nullable = false, precision=2, scale=2)
    private Float limit_rev2;

    @Column(name="weight_param_A", nullable = false)
    private int weight_param_A;

    @Column(name="weight_param_B", nullable = false)
    private int weight_param_B;

    @Column(name="weight_param_C", nullable = false)
    private int weight_param_C;

    @Column(name="weight_param_D", nullable = false)
    private int weight_param_D;

    @Column(name="weight_param_E", nullable = false)
    private int weight_param_E;

    // This cascade and orphanRemoval means that all children rubrics will be removed when the learning process is removed
    @OneToMany(mappedBy = "learningProcess", cascade = CascadeType.ALL, orphanRemoval = true) // https://www.baeldung.com/delete-with-hibernate
    @JsonIgnore // https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion
    private Set<AssessmentRubric> rubricSet;

    @OneToMany(mappedBy = "learningProcess", cascade = CascadeType.ALL, orphanRemoval = true) // https://www.baeldung.com/delete-with-hibernate
    @JsonIgnore // https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion
    private List<UserGroup> user_group_list;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private LearningSupervisor learning_supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private LearningProcessStatus learning_process_status;

    public LearningProcess(long id, @Size(min = 3, max = 256) String name,
                           @Size(min = 1, max = 1024) String description,
                           LocalDateTime starting_date_time,
                           LocalDateTime end_date_time,
                           Boolean is_cal1_available,
                           Boolean is_cal2_available,
                           Boolean is_cal3_available,
                           Boolean is_calF_available,
                           Float limit_cal1, Float limit_cal2,
                           Float limit_rev1, Float limit_rev2,
                           int weight_param_A,
                           int weight_param_B,
                           int weight_param_C,
                           int weight_param_D,
                           int weight_param_E,
                           LearningSupervisor supervisor,
                           LearningProcessStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.starting_date_time = starting_date_time;
        this.end_date_time = end_date_time;
        this.is_cal1_available = is_cal1_available;
        this.is_cal2_available = is_cal2_available;
        this.is_cal3_available = is_cal3_available;
        this.is_calF_available = is_calF_available;
        this.limit_cal1 = limit_cal1;
        this.limit_cal2 = limit_cal2;
        this.limit_rev1 = limit_rev1;
        this.limit_rev2 = limit_rev2;
        this.weight_param_A = weight_param_A;
        this.weight_param_B = weight_param_B;
        this.weight_param_C = weight_param_C;
        this.weight_param_D = weight_param_D;
        this.weight_param_E = weight_param_E;
        this.learning_supervisor = supervisor;
        this.learning_process_status = status;
    }

    public LearningProcess() { }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStarting_date_time() {
        return starting_date_time;
    }

    public void setStarting_date_time(LocalDateTime starting_date_time) { this.starting_date_time = starting_date_time; }

    public LocalDateTime getEnd_date_time() {
        return end_date_time;
    }

    public void setEnd_date_time(LocalDateTime end_date_time) {
        this.end_date_time = end_date_time;
    }

    public Boolean getIs_cal1_available() {
        return is_cal1_available;
    }

    public void setIs_cal1_available(Boolean is_cal1_available) {
        this.is_cal1_available = is_cal1_available;
    }

    public Boolean getIs_cal2_available() {
        return is_cal2_available;
    }

    public void setIs_cal2_available(Boolean is_cal2_available) {
        this.is_cal2_available = is_cal2_available;
    }

    public Boolean getIs_cal3_available() {
        return is_cal3_available;
    }

    public void setIs_cal3_available(Boolean is_cal3_available) {
        this.is_cal3_available = is_cal3_available;
    }

    public Boolean getIs_calF_available() {
        return is_calF_available;
    }

    public void setIs_calF_available(Boolean is_calF_available) {
        this.is_calF_available = is_calF_available;
    }

    public Float getLimit_cal1() {
        return limit_cal1;
    }

    public void setLimit_cal1(Float limit_cal1) {
        this.limit_cal1 = limit_cal1;
    }

    public Float getLimit_cal2() {
        return limit_cal2;
    }

    public void setLimit_cal2(Float limit_cal2) {
        this.limit_cal2 = limit_cal2;
    }

    public Float getLimit_rev1() {
        return limit_rev1;
    }

    public void setLimit_rev1(Float limit_rev1) {
        this.limit_rev1 = limit_rev1;
    }

    public Float getLimit_rev2() {
        return limit_rev2;
    }

    public void setLimit_rev2(Float limit_rev2) {
        this.limit_rev2 = limit_rev2;
    }

    public int getWeight_param_A() {
        return weight_param_A;
    }

    public void setWeight_param_A(int weight_param_A) {
        this.weight_param_A = weight_param_A;
    }

    public int getWeight_param_B() {
        return weight_param_B;
    }

    public void setWeight_param_B(int weight_param_B) {
        this.weight_param_B = weight_param_B;
    }

    public int getWeight_param_C() {
        return weight_param_C;
    }

    public void setWeight_param_C(int weight_param_C) {
        this.weight_param_C = weight_param_C;
    }

    public int getWeight_param_D() {
        return weight_param_D;
    }

    public void setWeight_param_D(int weight_param_D) {
        this.weight_param_D = weight_param_D;
    }

    public int getWeight_param_E() {
        return weight_param_E;
    }

    public void setWeight_param_E(int weight_param_E) {
        this.weight_param_E = weight_param_E;
    }

    public LearningSupervisor getLearning_supervisor() {
        return learning_supervisor;
    }

    public void setLearning_supervisor(LearningSupervisor learning_supervisor) {
        this.learning_supervisor = learning_supervisor;
    }

    public LearningProcessStatus getLearning_process_status() {
        return learning_process_status;
    }

    public void setLearning_process_status(LearningProcessStatus learning_process_status) {
        this.learning_process_status = learning_process_status;
    }

    public void setUserGroupList(List<UserGroup> user_group_list) {
        this.user_group_list = user_group_list;
    }

    public List<UserGroup> getUserGroupList() { return user_group_list; }

    public void addUserGroup(UserGroup userGroup) {
        if (this.user_group_list == null) {
            this.user_group_list = new ArrayList<>();
        }
        this.user_group_list.add(userGroup);
    }

    public void removeUserGroup(UserGroup userGroup) {
        if (this.user_group_list == null) {
            this.user_group_list = new ArrayList<>();
        }
        if (this.user_group_list.contains(userGroup)) {
            this.user_group_list.remove(userGroup);
        }
    }

    public Set<AssessmentRubric> getRubricSet() { return rubricSet; }

    public void addRubric(AssessmentRubric rubric) {
        if (this.rubricSet == null) {
            this.rubricSet = new HashSet<>();
        }
        this.rubricSet.add(rubric);
    }


    @Override
    public String toString(){
        String info = "";

        JSONObject jsonInfo = new JSONObject();
        try {
            jsonInfo.put("id",this.id);
            jsonInfo.put("name",this.name);
            jsonInfo.put("description",this.description);
            jsonInfo.put("starting_date_time",this.starting_date_time);
            jsonInfo.put("end_date_time",this.end_date_time);
            jsonInfo.put("is_cal1_available",this.is_cal1_available);
            jsonInfo.put("is_cal2_available",this.is_cal2_available);
            jsonInfo.put("is_cal3_available",this.is_cal3_available);
            jsonInfo.put("is_calF_available",this.is_calF_available);
            jsonInfo.put("limit_cal1",this.limit_cal1);
            jsonInfo.put("limit_cal2",this.limit_cal2);
            jsonInfo.put("limit_rev1",this.limit_rev1);
            jsonInfo.put("limit_rev2",this.limit_rev2);
            jsonInfo.put("weight_param_A",this.weight_param_A);
            jsonInfo.put("weight_param_B",this.weight_param_B);
            jsonInfo.put("weight_param_C",this.weight_param_C);
            jsonInfo.put("weight_param_D",this.weight_param_D);
            jsonInfo.put("weight_param_E",this.weight_param_E);
            jsonInfo.put("user_group_list", this.user_group_list);
            jsonInfo.put("learning_supervisor",this.learning_supervisor);
            jsonInfo.put("learning_process_status",this.learning_process_status);

        } catch (JSONException e) {
            e.getStackTrace();
        }

        info = jsonInfo.toString();
        return info;
    }
}
