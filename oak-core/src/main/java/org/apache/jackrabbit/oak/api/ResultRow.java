begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
package|;
end_package

begin_comment
comment|/**  * A query result row.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ResultRow
block|{
name|String
name|getPath
parameter_list|()
function_decl|;
name|String
name|getPath
parameter_list|(
name|String
name|selectorName
parameter_list|)
function_decl|;
name|CoreValue
name|getValue
parameter_list|(
name|String
name|columnName
parameter_list|)
function_decl|;
name|CoreValue
index|[]
name|getValues
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

