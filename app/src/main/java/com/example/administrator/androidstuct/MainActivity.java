package com.example.administrator.androidstuct;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.daolib.dao.BaseDao;
import com.example.daolib.dao.DaoFactory;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private BaseDao<User> baseDao;
    private String[] names;
    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseDao = DaoFactory.getInstance().getBaseDao(User.class);
        names = new String[]{"tyh", "zww", "tyg"};
    }

    /**
     * 插入
     */
    public void clickToInsert(View view) {
        if (baseDao == null) {
            return;
        }
        count++;
        User user = new User();
        for (int i = 0; i < names.length; i++) {
            user.setName(names[i]);
            user.setId(count);
            user.setMan(count%2==0);
            baseDao.insert(user);
        }
        Toast.makeText(this, "插入完成", Toast.LENGTH_SHORT).show();
    }

    /**
     * 更新
     */
    public void clickToUpdate(View view) {
        if (baseDao == null) {
            return;
        }
        User where = new User();
        where.setName(names[0]);
        where.setId(count);
        User entity = new User();
        entity.setId(-1);
        entity.setName("更新"+count);
        int update = baseDao.update(entity, where);
        Toast.makeText(this, "更新成功 = "+update, Toast.LENGTH_SHORT).show();
    }

    /**
     * 删除
     */
    public void clickToDelete(View view) {
        if (baseDao == null) {
            return;
        }
        User where = new User();
        where.setId(-1);
        int delete = baseDao.delete(where);
        Toast.makeText(this, "删除成功 = "+delete, Toast.LENGTH_SHORT).show();
    }

    /**
     * 查询
     */
    public void clickToQuery(View view) {
        if (baseDao == null) {
            return;
        }
        User where = new User();
        where.setName("tyh");
        List<User> list = baseDao.query(where);

        for (User user : list) {
            Log.e("---------",user.toString());
        }
        Toast.makeText(this, "查询完成 = "+ list.size(), Toast.LENGTH_SHORT).show();
    }
}
