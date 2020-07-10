# 1. Feature List

- 用户注册
- 获取用户信息
- 用户登录，获取JWT，包含accessToken和refreshToken
- 用户正常访问后台，校验accessToken
- 用户accessToken过期，根据refreshToken换取accessToken
- 用户登出
- 更新用户权限

# 2. Interface Design

## 2.1. 用户注册

- 功能说明：注册用户身份信息
- url: /register
- 行为：
    + 用户信息插入tbl_user表


## 2.2. 获取用户信息

- 功能说明：根据用户手机号获取用户信息
- url: /getUserByPhoneNumber
- 行为：
    + 从tbl_user表中获取用户信息


## 2.3. 用户登录

- 功能说明：用户登录，获取JWT，包含accessToken和refreshToken
- url: /login
- 行为：
    + 从tbl_user表中获取用户信息检查合法性
    + 校验成功返回accessToken和refreshToken
    + refreshToken与用户的映射关系存入内存，系统权限和用户映射关系也存入内存

## 2.4. 校验accessToken

- 功能说明：用户正常访问后台时，校验携带的accessToken是否合法
- url: /checkAccess
- 行为：
    + 校验accessToken的签名合法性、时间是否过期、是否有对应系统的访问权限


## 2.5. 根据refreshToken换取accessToken

- 功能说明：用户accessToken过期，根据refreshToken换取accessToken
- url: /refresh
- 行为：
    + 从内存中取出refreshToken，校验refreshToken签名合法性，和服务器保存的一致性，是否过期
    + 校验成功后，生成新的accessToken返回

## 2.6. 用户登出

- 功能说明：用户登出，清除服务器用户登录记录
- url: /logout
- 行为：
    + 删除内存中refreshToken和用户的映射关系，删除该用户内存中其余信息

## 2.7. 更新用户权限

- 功能说明：更新用户权限
- url: /authorize
- 行为：
    + 删除内存中用户的权限信息
    + 修改数据库中用户的权限信息
    + 在内存中添加新的用户权限信息





