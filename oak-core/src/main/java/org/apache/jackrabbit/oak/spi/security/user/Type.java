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
name|spi
operator|.
name|security
operator|.
name|user
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|UserManager
import|;
end_import

begin_comment
comment|/**  * Created by IntelliJ IDEA.  * User: angela  * Date: 8/22/12  * Time: 3:48 PM  * To change this template use File | Settings | File Templates.  */
end_comment

begin_enum
specifier|public
enum|enum
name|Type
block|{
name|USER
parameter_list|(
name|UserManager
operator|.
name|SEARCH_TYPE_USER
parameter_list|)
operator|,
constructor|GROUP(UserManager.SEARCH_TYPE_GROUP
block|)
enum|,
name|AUTHORIZABLE
argument_list|(
name|UserManager
operator|.
name|SEARCH_TYPE_AUTHORIZABLE
argument_list|)
enum|;
end_enum

begin_decl_stmt
specifier|private
specifier|final
name|int
name|userType
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|Type
argument_list|(
name|int
name|userType
argument_list|)
block|{
name|this
operator|.
name|userType
operator|=
name|userType
block|;     }
end_expr_stmt

unit|}
end_unit

