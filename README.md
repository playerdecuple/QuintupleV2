# QuintupleV2
> **퀸튜플**의 2.0 버전입니다. (코드네임 : Easier-QUINTUPLE or EQ)  
> **Copyright (c) 2020 TEAM DECUPLE. All rights reserved.**

## QuintupleV2의 변경 사항
* 일부 쓰이지 않는 기능이 제거됩니다.
* 뮤직 봇 기능이 개편됩니다.
* 명령어를 쉽게 만듭니다.
* 깨끗한 텍스트 채널을 만들기 위해 메시지를 삭제하게 됩니다.

## QuintupleV2 저작 사용 조건
QuintupleV2의 소스 코드는 오픈소스로서, 여러분들이 사용하는 것은 무관합니다. 하지만, QuintupleV2를 바탕으로 만들었거나 QuintupleV2의 소스 코드를 사용했다면 다음과 같은 조건을 만족해야 합니다.
* 모든 소스 코드에 다음과 같은 내용을 써 넣으십시오.
```
/*
 * Copyright (c) 2020 Team Decuple. All right reserved.
 * This code is based on source code of 'QuintupleV2'.
 */
 ```
 * 모든 명령어 중에 전체 유저가 사용할 수 있는 명령어 한 개의 설명에 다음과 같은 내용을 써 넣으십시오.
 ```
 This bot is based on source code of 'QuintupleV2'.
 또는 "이 봇은 'QuintupleV2'를 기반으로 만들어졌습니다.'
 ```
 * QuintupleV2의 소스 코드는 비영리적 목적으로 사용되어야만 합니다.
 * QuintupleV2의 저작권은 Team Decuple에게 있음을 표기해 주십시오.
 
 ## QuintupleV2에서 추가된 클래스들
 * EasyEqual : 번거로이 equalsIgnoreCase()를 일일이 쓸 필요를 없앴습니다. [basicText]와 [otherText(중 하나)]가 같을 경우 무조건 true를 return합니다.
 ```java
 EasyEqual e = new EasyEqual();
 
 if (e.eq([basicText], [otherText1], [otherText2] ...) {
    // execute codes
 }
 ```
 * ReadFile, WriteFile, DeleteFile : 파일의 읽기, 쓰기, 제거를 담당하는 클래스입니다.
 ```java
 ReadFile r = new ReadFile();
 WriteFile w = new WriteFile();
 DeleteFile d = new DeleteFile();
 
 String path = "INPUT_PATH";
 File f = new File(path);
 
 r.readString(path); // can be replace 'r,readString(f);'
 r.readInt(path); // can be replace 'r.readInt(f);'
 r.readLong(path); // can be replace 'r.readLong(f);;'
 
 w.writeString(path);
 w.writeInt(path);
 w.writeLong(path);
 
 d.deleteFile(f);
 
 // path can be replace to 'f'.
 ```
 * API Package 내에 있는 클래스들 : API를 불러오는 클래스들입니다.
 
 # 마치며
 Team Decuple은 Project: Decuple - QuintupleV2를 열심히 제작하고 있습니다.
 여러분들의 도움으로 인해 개발을 더욱더 빠르게, 완성도 있게 끝낼지도 모릅니다.
 팀 데큐플을, 부디 도와주셨으면 합니다. 감사합니다.
