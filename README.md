# QuintupleV2
> **퀸튜플**의 2.0 버전입니다. (코드네임 : Easier-QUINTUPLE or EQ)  
> **Copyright (c) 2020 TEAM DECUPLE. All rights reserved.**

## QuintupleV2의 변경 사항
* 일부 쓰이지 않는 기능이 제거됩니다.
* 뮤직 봇 기능이 개편됩니다.
* 명령어를 쉽게 만듭니다.
* 깨끗한 텍스트 채널을 만들기 위해 메시지를 삭제하게 됩니다.

## QuintupleV2 License
* MIT License
 
 ## QuintupleV2에서 추가된 클래스들
 * EasyEqual : 번거로이 equalsIgnoreCase()를 일일이 쓸 필요를 없앴습니다. [basicText]와 [otherText(중 하나)]가 같을 경우 무조건 true를 return합니다.
 ```java
 EasyEqual e = new EasyEqual();
 
 if (e.eq([basicText], [otherText1], [otherText2] ...) {
    // execute codes
 }
 ```
 * ReadFile, WriteFile, DeleteFile : 파일의 읽기, 쓰기, 제거를 담당하는 클래스입니다.
  - 2.0.73.4 : CopyFile이 추가되었습니다. (파일의 복사, 일괄 복사를 담당하는 클래스입니다.)
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
