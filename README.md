[![Release](https://jitpack.io/v/ashLikun/VerifyCodeView.svg)](https://jitpack.io/#ashLikun/VerifyCodeView)

# **VerifyCodeView**
项目简介
    模仿滴滴的验证码输入
## 使用方法

build.gradle文件中添加:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
并且:

```gradle
dependencies {
    compile 'com.github.ashLikun:VerifyCodeView:{latest version}'
}
```

## 详细介绍

    <!--输入框类型-->
    <attr name="vcode_inputType" format="enum">
        <enum name="number" value="1"></enum>
        <enum name="text" value="2"></enum>
        <enum name="password" value="3"></enum>
        <enum name="phone" value="4"></enum>
    </attr>
    <!--验证码个数-->
    <attr name="vcode_codeNumber" format="integer" />
    <!--每个输入框是否要正方形,默认true   不是正方形，并且没有指定高度就强制设置高度(40),要不就指定高度-->
    <attr name="vcode_isSquare" format="boolean" />
    <attr name="vcode_textSize" format="dimension" />
    <attr name="vcode_textColor" format="color" />
    <!--间距宽度-->
    <attr name="vcode_spacingWidth" format="dimension" />
    <!--获取焦点的背景-->
    <attr name="vcode_focusDrawable" format="reference" />
    <!--普通背景-->
    <attr name="vcode_normalDrawable" format="reference" />

        
        

    如果要改变边框，请替换这两个文件，就是本地也建立一样的文件
    verif_code_bg_focus.xml
    verif_code_bg_normal.xml
    
```java    
 codeView.setListener(new VerifyCodeView.OnCompleteListener() {
            @Override
            public void onComplete(String code) {
                Toast.makeText(MainActivity.this, code, Toast.LENGTH_LONG).show();
            }
        });
```