package com.example.prac_ss.dto;

//enum 클래스 객체는 특별함. 상수값을 저장해놓는 객체이고 같은 페키지 안에 있을 땐 import 없이 사용 가능
public enum UserRole {
    
    //상수 정의 해당 값은 values()로 모두 가져올 수 있음
    ROLE_USER("user"),
    ROLE_ADMIN("admin");

    //final을 쓰면 한 번 정의하면 못바꿈.
    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    // "ROLE_USER --> "user" 형식으로 변환
    public static String fromRoleName(String name) {
        //모든 상수의 개수만큼 실행
        for (UserRole role : values()) {
            //각 상수의 이름을 받아온 name과 비교
            if (role.name().equals(name)) {
                //후 같으면 해당상수의 roleName을 반환 "user" or "admin"
                return role.getRoleName();
            }
        }
        //위 리턴이 실행되지 않으면(name의 값이 "user"도, "admin"도 아니면 아래 코드 실행
        return name.replace("ROLE_", "").toUpperCase();
    }

    // "user" --> "ROLE_USER" 형식으로 변환
    public static String toRoleName(String roleName) {
        for (UserRole role : values()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return role.name();
            }
        }
        return "ROLE_" + roleName.toUpperCase();
    }
}