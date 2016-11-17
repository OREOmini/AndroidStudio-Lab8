# AndroidStudio-Lab8

## 实验八 数据存储(二)


### 实验目的			

1.    学习 SQLite 数据库的使用;

2.    学习ContentProvider的使用;

3.    复习Android界面编程。


      ​		


### 实验内容

#### 界面逻辑

**首次启动界面**有一个button和一个listView，listView用SimpleAdapter来实现，三个id分别是name，birthday，present

用header的Map设置首行

```java
List<Map<String, String>> data = new ArrayList<>();
Map<String, String> header = new LinkedHashMap<>();
header.put("name", "姓名");
header.put("birthday", "生日");
header.put("present", "礼物");
data.add(header);

Map<String, String> temp = new LinkedHashMap<>();
temp.put("name", "brenda");
temp.put("birthday", "1996/4/15");
temp.put("present", "money");
data.add(temp);

ListView list = (ListView) findViewById(R.id.list);
SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.item,
        new String[] {"name", "birthday", "present"}, new int[] {R.id.name, R.id.birthday, R.id.present});
list.setAdapter(simpleAdapter);
```

 ![屏幕快照 2016-11-16 下午10.29.26](/Users/wangyunwen/Desktop/ScreenShoot/屏幕快照 2016-11-16 下午10.29.26.png)



**点击<增加条目>按钮跳转到次界面**

```java
Button add = (Button) findViewById(R.id.add);
add.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        startActivity(intent);
    }
});
```



**次界面**

 ![屏幕快照 2016-11-16 下午11.13.46](/Users/wangyunwen/Desktop/ScreenShoot/屏幕快照 2016-11-16 下午11.13.46.png)

姓名不能为空，并把输入的值传给主界面

```java
addItem.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String name = getName.getText().toString();
        if (name.equals("")) {
            Toast.makeText(AddActivity.this, "名字为空，请完善",Toast.LENGTH_SHORT).show();
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putString("birthday", getBirthday.getText().toString());
            bundle.putString("present", getPresent.getText().toString());
            Intent intent = new Intent(AddActivity.this, MainActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
});
```



**点击listItem的编辑界面**，用.setView(R.layout.dialog_layout)来自定义界面

```java
Dialog dialog = new AlertDialog.Builder(MainActivity.this)
        .setTitle("编辑信息")
        .setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        })
        .setNegativeButton("放弃修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        })
        .setView(R.layout.dialog_layout)
        .create();
dialog.show();
```

 ![屏幕快照 2016-11-17 上午12.10.13](ScreenShoot/屏幕快照 2016-11-17 上午12.10.13.png)

还要往界面传入保存的参数

```java
View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
Map<String, Object> mMap = (Map<String, Object>) simpleAdapter.getItem(position);

TextView showName = (TextView) view1.findViewById(R.id.showName);
showName.setText(mMap.get("name").toString());
EditText setBirthday = (EditText) view1.findViewById(R.id.setBirthday);
setBirthday.setText(mMap.get("birthday").toString());
EditText setPresent = (EditText) view1.findViewById(R.id.setPresent);
setPresent.setText(mMap.get("present").toString());
```

 ![屏幕快照 2016-11-17 上午9.05.25](ScreenShoot/屏幕快照 2016-11-17 上午9.05.25.png)



#### SQlite数据库使用

**建立myDB类来实现SQLiteOpenHelper的子类**，除主键外有三个内容name TEXT, birthday TEXT, present TEXT，对应姓名，生日，礼物

```java
public class myDB extends SQLiteOpenHelper {

    private static final String DB_NAME = "BirthdayMemo";
    private static final String TABLE_NAME = "BirthdayMemo";

    public myDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CTREATE_TABLE = "CREATE TABLE if not exists "
                + TABLE_NAME
                + "(id integer primary key, name TEXT, birthday TEXT, present TEXT)";
        sqLiteDatabase.execSQL(CTREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }
}		
```

在MainActivity中需要多次更新ListView，所以用一个函数**每次从数据库中读取数据来实现数据库的更新**，每次先加入一个header作为表头目录行

```java
Map<String, String> header = new LinkedHashMap<>();
header.put("name", "姓名");
header.put("birthday", "生日");
header.put("present", "礼物");
data.add(header);
```

然后用Cursor来遍历数据库，得到数据库中的值，加入到data中

```java
db = myDB.getReadableDatabase();
Cursor cursor = db.query(TABLE_NAME, new String[] {"name", "birthday", "present"},
        null, null, null, null, null);
while (cursor.moveToNext()) {
    Map<String, String> temp = new LinkedHashMap<>();
    int nameCol = cursor.getColumnIndex("name");
    int birthdayCol = cursor.getColumnIndex("birthday");
    int presentCol = cursor.getColumnIndex("present");
    String name = cursor.getString(nameCol);
    String birthday = cursor.getString(birthdayCol);
    String present = cursor.getString(presentCol);
    temp.put("name", name);
    temp.put("birthday", birthday);
    temp.put("present", present);
    data.add(temp);
}
```

再用SimpleAdapter把数据填进ListView

```java
list = (ListView) findViewById(R.id.list);
simpleAdapter = new SimpleAdapter(this, data, R.layout.item,
        new String[] {"name", "birthday", "present"}, new int[] {R.id.name, R.id.birthday, R.id.present});
list.setAdapter(simpleAdapter);

db.close();
```



**在AddActivity中实现添加新的生日**

```java
Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("birthday", getBirthday.getText().toString());
                    bundle.putString("present", getPresent.getText().toString());
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
```

 ![屏幕快照 2016-11-17 下午2.03.20](ScreenShoot/屏幕快照 2016-11-17 下午2.03.20.png)



MainActivity中，**用ContentValues来实现db的插入**

```java
private void updateListAndDB(String name, String birthday, String present) {
    ContentValues cv = new ContentValues();
    cv.put("name", name);
    cv.put("birthday", birthday);
    cv.put("present", present);
    db.insert(DB_NAME, null, cv);
    db.close();
    setListAdapter();
}
```

 ![屏幕快照 2016-11-17 下午2.03.29](ScreenShoot/屏幕快照 2016-11-17 下午2.03.29.png)



也要遍历然后**判断姓名是否重复**

```java
private boolean nameExist(String name) {
    boolean f = false;
    db = myDB.getReadableDatabase();
    Cursor cursor = db.query(TABLE_NAME, new String[] {"name", "birthday", "present"},
            null, null, null, null, null);
    while (cursor.moveToNext()) {
        int nameCol = cursor.getColumnIndex("name");
        String nameTemp = cursor.getString(nameCol);
        if(name.equals(nameTemp))
            f = true;
    }
    return f;
}
```

 ![屏幕快照 2016-11-17 下午2.12.24](ScreenShoot/屏幕快照 2016-11-17 下午2.12.24.png)



**当点击Item的时候可以保存修改的内容到数据库**，用ContentValues和where语句来实现update，然后调用setListAdapter()更新ListView

```java
Dialog dialog = new AlertDialog.Builder(MainActivity.this)
        .setTitle("编辑信息")
        .setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db = myDB.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("name", mMap.get("name").toString());
                cv.put("birthday", setBirthday.getText().toString());
                cv.put("present", setPresent.getText().toString());
                String whereClause = "name=?";
                String[] whereArgs = {mMap.get("name").toString()};
                db.update(TABLE_NAME, cv, whereClause, whereArgs);
                setListAdapter();
            }
        })
        .setNegativeButton("放弃修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        })
        .setView(view1)
        .create();
```

 ![屏幕快照 2016-11-17 下午2.13.48](ScreenShoot/屏幕快照 2016-11-17 下午2.13.48.png)

 ![屏幕快照 2016-11-17 下午2.13.56](ScreenShoot/屏幕快照 2016-11-17 下午2.13.56.png)



**长按可删除该生日条目**，用where语句来进行删除

```java
public AdapterView.OnItemLongClickListener onLongClick = new AdapterView.OnItemLongClickListener() {
    @Override
    public boolean onItemLongClick (AdapterView < ? > parent, View view,int position, long id){
        final Map<String, Object> mMap = (Map<String, Object>) simpleAdapter.getItem(position);
        Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("是否删除")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = mMap.get("name").toString();
                        db = myDB.getWritableDatabase();
                        String whereClause = "name=?";
                        String[] whereArgs = {name};
                        db.delete(TABLE_NAME, whereClause, whereArgs);
                        setListAdapter();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                })
                .create();
        dialog.show();
        return false;
    }
};
```

 ![屏幕快照 2016-11-17 下午2.14.59](ScreenShoot/屏幕快照 2016-11-17 下午2.14.59.png)

 ![屏幕快照 2016-11-17 下午2.15.06](ScreenShoot/屏幕快照 2016-11-17 下午2.15.06.png)



#### Content Provider 使用

**在 AndroidManifest.xml 文件里声明读取通讯录的权限**

```xml
<uses-permission android:name="android.permission.READ_CONTACTS"/>
```

跟pdf给的方法不太一样，我是用一个cursor得到全部联系人信息

```java
Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
        null, null, null, null);
```

然后对所有联系人进行遍历，看有没有相同名字的，有的话就取出该条联系人信息中的电话号码

```java
while (cursor.moveToNext()) {
    // 获得联系人的ID号
    int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
    String contactId = cursor.getString(idColumn);
    //获取联系人的名字
    String contactName = cursor.getString(cursor.getColumnIndex
            (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
    if(name.equals(contactName)) {
        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
        while (c.moveToNext()) {
            result +=
                    c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + "  ";
        }
        c.close();
    }
}
```

 ![屏幕快照 2016-11-17 下午4.18.00](ScreenShoot/屏幕快照 2016-11-17 下午4.18.00.png) ![屏幕快照 2016-11-17 下午4.18.17](ScreenShoot/屏幕快照 2016-11-17 下午4.18.17.png)



### 实验总结

* 在MainActivity中要用到dialog里面的id，必须加上

  ```java
  View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
  TextView showName = (TextView) view1.findViewById(R.id.showName);
  ```

  来得到控件，不加view1.的话就会默认是activity_main里面的id，得到的控件就会为空

* 在点击编辑的界面中，使用

  ```java
  buider.setView(R.layout.dialog_layout)
  ```

  并不能得到上面对于item信息的加入，只有空白的输入行，要使用

  ```
  .setView(view1)
  ```

  才能正常载入

* 数据库的遍历：使用Cursor

  >  移动光标到下一行,到最后使返回false
  >
  > boolean moveToNext();

  访问 Cursor 的下标获得其中的数据

  ```java
  int nameColumnIndex = cur.getColumnIndex(People.NAME);
  String name = cur.getString(nameColumnIndex);
  ```

* 数据库的删除

  ```java
  db = myDB.getWritableDatabase();
   String whereClause = "name=?";
  String[] whereArgs = {name};
  db.delete(TABLE_NAME, whereClause, whereArgs);
  ```

* 数据库的插入

  ```java
  ContentValues cv = new ContentValues();
      cv.put("name", name);
      cv.put("birthday", birthday);
      cv.put("present", present);
      db.insert(DB_NAME, null, cv);
      db.close();在AndroidManifest.xml中应该把<uses-permission android:name="android.permission.READ_CONTACTS"/>放在application的外面
  ```

  ​

* 尝试读取联系人报错

  > opening provider com.android.providers.contacts.ContactsProvider2 from ProcessRecord{2cc2e494 9045:com.example.wangyunwen.ex08/u0a66} (pid=9045, uid=10066) requires android.permission.READ_CONTACTS or android.permission.WRITE_CONTACTS

  在AndroidManifest.xml中应该把<uses-permission android:name="android.permission.READ_CONTACTS"/>放在application的外面

   ![屏幕快照 2016-11-17 下午3.28.26](ScreenShoot/屏幕快照 2016-11-17 下午3.28.26.png)



* ```java
  int isHas = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContr act.Contacts.HAS_PHONE_NUMBER)));
  ```

  pdf上给的这个会报错

  > android.database.CursorIndexOutOfBoundsException: Index -1 requested, with a size of 4

  关于Cursor

  > public abstract int getColumnIndex (String columnName)
  > Since: API Level 1
  > Returns the zero-based index for the given column name, or -1 if the column doesn't exist. If you expect the column to exist use getColumnIndexOrThrow(String) instead, which will make the error more clear.

  http://blog.csdn.net/hehuanluoluo/article/details/6231097 中说到

  > 文档里清楚的表明：在要读取的列不存在的时候该方法会返回值“-1”。所以可知，以上报错可能是因为要get的列不存在，也可能是因为游标位置不对。后来发现，因为我在执行这个语句前没有执行“Cursor.moveToNext();”这个函数，导致游标还位于第一位置的前面，所以索引显示为“-1”,前面加上这句就没错了。
