package xmu.springBootMybatis.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xmu.springBootMybatis.entity.UserRole;

@Mapper
public interface UserRoleMapper {
	
	@Select("select * from user_role where userId = #{userId}")
	public UserRole getUserRoleByUser(@Param("userId") long userId);

	@Insert("insert into user_role (userId,roleId) values(#{userId},#{roleId})")
	public int insertUserRole(@Param("userId") long userId,@Param("roleId") int roleId);
}
