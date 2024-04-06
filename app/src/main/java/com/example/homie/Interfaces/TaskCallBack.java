package com.example.homie.Interfaces;

import com.example.homie.Models.Task;

public interface TaskCallBack {

    void editTaskClicked(Task task, int position);
    void taskCheckedClicked(Task task, int position);
}
