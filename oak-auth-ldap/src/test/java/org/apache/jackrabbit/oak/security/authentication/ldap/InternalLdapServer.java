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
name|authentication
operator|.
name|ldap
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|BasicAttributes
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|DirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|ldap
operator|.
name|LdapContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|constants
operator|.
name|ServerDNConstants
import|;
end_import

begin_class
class|class
name|InternalLdapServer
extends|extends
name|AbstractServer
block|{
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_MEMBER_ATTR
init|=
literal|"member"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_CLASS_ATTR
init|=
literal|"groupOfNames"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ADMIN_PW
init|=
literal|"secret"
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|doDelete
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
specifier|public
name|String
name|addUser
parameter_list|(
name|String
name|firstName
parameter_list|,
name|String
name|lastName
parameter_list|,
name|String
name|userId
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|cn
init|=
name|firstName
operator|+
literal|' '
operator|+
name|lastName
decl_stmt|;
name|String
name|dn
init|=
name|buildDn
argument_list|(
name|cn
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|StringBuilder
name|entries
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|entries
operator|.
name|append
argument_list|(
literal|"dn: "
argument_list|)
operator|.
name|append
argument_list|(
name|dn
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
literal|"objectClass: inetOrgPerson\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"cn: "
argument_list|)
operator|.
name|append
argument_list|(
name|cn
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
literal|"sn: "
argument_list|)
operator|.
name|append
argument_list|(
name|lastName
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
literal|"givenName:"
argument_list|)
operator|.
name|append
argument_list|(
name|firstName
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
literal|"uid: "
argument_list|)
operator|.
name|append
argument_list|(
name|userId
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
literal|"userPassword: "
argument_list|)
operator|.
name|append
argument_list|(
name|password
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|injectEntries
argument_list|(
name|entries
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|dn
return|;
block|}
specifier|public
name|String
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|dn
init|=
name|buildDn
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|StringBuilder
name|entries
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|entries
operator|.
name|append
argument_list|(
literal|"dn: "
argument_list|)
operator|.
name|append
argument_list|(
name|dn
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
literal|"objectClass: "
argument_list|)
operator|.
name|append
argument_list|(
name|GROUP_CLASS_ATTR
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
name|GROUP_MEMBER_ATTR
argument_list|)
operator|.
name|append
argument_list|(
literal|":\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"cn: "
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|injectEntries
argument_list|(
name|entries
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|dn
return|;
block|}
specifier|public
name|void
name|addMember
parameter_list|(
name|String
name|groupDN
parameter_list|,
name|String
name|memberDN
parameter_list|)
throws|throws
name|Exception
block|{
name|LdapContext
name|ctxt
init|=
name|getWiredContext
argument_list|()
decl_stmt|;
name|BasicAttributes
name|attrs
init|=
operator|new
name|BasicAttributes
argument_list|()
decl_stmt|;
name|attrs
operator|.
name|put
argument_list|(
literal|"member"
argument_list|,
name|memberDN
argument_list|)
expr_stmt|;
name|ctxt
operator|.
name|modifyAttributes
argument_list|(
name|groupDN
argument_list|,
name|DirContext
operator|.
name|ADD_ATTRIBUTE
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeMember
parameter_list|(
name|String
name|groupDN
parameter_list|,
name|String
name|memberDN
parameter_list|)
throws|throws
name|Exception
block|{
name|LdapContext
name|ctxt
init|=
name|getWiredContext
argument_list|()
decl_stmt|;
name|BasicAttributes
name|attrs
init|=
operator|new
name|BasicAttributes
argument_list|()
decl_stmt|;
name|attrs
operator|.
name|put
argument_list|(
literal|"member"
argument_list|,
name|memberDN
argument_list|)
expr_stmt|;
name|ctxt
operator|.
name|modifyAttributes
argument_list|(
name|groupDN
argument_list|,
name|DirContext
operator|.
name|REMOVE_ATTRIBUTE
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|loadLdif
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|loadLdif
argument_list|(
name|in
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|buildDn
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isGroup
parameter_list|)
block|{
name|StringBuilder
name|dn
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|dn
operator|.
name|append
argument_list|(
literal|"cn="
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
if|if
condition|(
name|isGroup
condition|)
block|{
name|dn
operator|.
name|append
argument_list|(
name|ServerDNConstants
operator|.
name|GROUPS_SYSTEM_DN
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dn
operator|.
name|append
argument_list|(
name|ServerDNConstants
operator|.
name|USERS_SYSTEM_DN
argument_list|)
expr_stmt|;
block|}
return|return
name|dn
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

