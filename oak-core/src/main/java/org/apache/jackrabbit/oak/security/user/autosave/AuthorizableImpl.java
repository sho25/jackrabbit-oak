begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|security
operator|.
name|user
operator|.
name|autosave
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Object
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Override
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|String
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

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
name|Authorizable
import|;
end_import

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
name|Group
import|;
end_import

begin_class
class|class
name|AuthorizableImpl
implements|implements
name|Authorizable
block|{
specifier|final
name|Authorizable
name|dlg
decl_stmt|;
specifier|final
name|AutoSaveEnabledManager
name|mgr
decl_stmt|;
name|AuthorizableImpl
parameter_list|(
name|Authorizable
name|dlg
parameter_list|,
name|AutoSaveEnabledManager
name|mgr
parameter_list|)
block|{
name|this
operator|.
name|dlg
operator|=
name|dlg
expr_stmt|;
name|this
operator|.
name|mgr
operator|=
name|mgr
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getID
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|dlg
operator|.
name|getID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGroup
parameter_list|()
block|{
return|return
name|dlg
operator|.
name|isGroup
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Principal
name|getPrincipal
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|dlg
operator|.
name|getPrincipal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Group
argument_list|>
name|declaredMemberOf
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|AuthorizableWrapper
operator|.
name|createGroupIterator
argument_list|(
name|dlg
operator|.
name|declaredMemberOf
argument_list|()
argument_list|,
name|mgr
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Group
argument_list|>
name|memberOf
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|AuthorizableWrapper
operator|.
name|createGroupIterator
argument_list|(
name|dlg
operator|.
name|memberOf
argument_list|()
argument_list|,
name|mgr
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|dlg
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|mgr
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getPropertyNames
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|dlg
operator|.
name|getPropertyNames
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getPropertyNames
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|dlg
operator|.
name|getPropertyNames
argument_list|(
name|s
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|dlg
operator|.
name|hasProperty
argument_list|(
name|s
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|s
parameter_list|,
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|dlg
operator|.
name|setProperty
argument_list|(
name|s
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|mgr
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|s
parameter_list|,
name|Value
index|[]
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|dlg
operator|.
name|setProperty
argument_list|(
name|s
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|mgr
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|getProperty
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|dlg
operator|.
name|getProperty
argument_list|(
name|s
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removeProperty
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
return|return
name|dlg
operator|.
name|removeProperty
argument_list|(
name|s
argument_list|)
return|;
block|}
finally|finally
block|{
name|mgr
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|dlg
operator|.
name|getPath
argument_list|()
return|;
block|}
comment|//-------------------------------------------------------------< Object>---
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|dlg
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|AuthorizableImpl
condition|)
block|{
return|return
name|dlg
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|AuthorizableImpl
operator|)
name|o
operator|)
operator|.
name|dlg
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|dlg
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

