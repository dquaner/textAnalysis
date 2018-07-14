package xmu.springBootMybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import xmu.springBootMybatis.entity.Info;

@Mapper
public interface InfoMapper {
	@Select("select * from papersummary where project_id = #{project_id}")
	public Info getInfoByProjectId(@Param("project_id")long project_id);
	
}
