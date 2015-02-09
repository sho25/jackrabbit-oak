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
name|jcr
operator|.
name|security
operator|.
name|authorization
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
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
name|nodetype
operator|.
name|NodeType
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
name|JcrConstants
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
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
name|test
operator|.
name|api
operator|.
name|util
operator|.
name|Text
import|;
end_import

begin_class
specifier|public
class|class
name|ReadNodeTypeTest
extends|extends
name|AbstractEvaluationTest
block|{
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-2441">OAK-2441</a>      */
specifier|public
name|void
name|testNodeGetPrimaryType
parameter_list|()
throws|throws
name|Exception
block|{
name|deny
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|propertyExists
argument_list|(
name|path
operator|+
literal|'/'
operator|+
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
expr_stmt|;
name|NodeType
name|primary
init|=
name|n
operator|.
name|getPrimaryNodeType
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|primary
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-2441">OAK-2441</a>      */
specifier|public
name|void
name|testNodeGetMixinTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|addMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_LOCKABLE
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|propertyExists
argument_list|(
name|path
operator|+
literal|'/'
operator|+
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
argument_list|)
expr_stmt|;
name|deny
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|propertyExists
argument_list|(
name|path
operator|+
literal|'/'
operator|+
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|noMixins
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|getMixinNodeTypes
argument_list|()
operator|.
name|length
decl_stmt|;
name|NodeType
index|[]
name|mixins
init|=
name|n
operator|.
name|getMixinNodeTypes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|noMixins
argument_list|,
name|mixins
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      * Verify that transient changes to jcr:mixinTypes are reflected in the      * API call {@link javax.jcr.Node#getMixinNodeTypes()}.      */
specifier|public
name|void
name|testNodeGetMixinTypesWithTransientModifications
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|noMixins
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|getMixinNodeTypes
argument_list|()
operator|.
name|length
decl_stmt|;
name|Node
name|node
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|node
operator|.
name|addMixin
argument_list|(
name|NodeType
operator|.
name|MIX_CREATED
argument_list|)
expr_stmt|;
name|NodeType
index|[]
name|mixins
init|=
name|node
operator|.
name|getMixinNodeTypes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|noMixins
operator|+
literal|1
argument_list|,
name|mixins
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-2488">OAK-2488</a>      */
specifier|public
name|void
name|testGetPrimaryTypeFromNewNode
parameter_list|()
throws|throws
name|Exception
block|{
name|deny
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|Node
name|newNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|testRoot
argument_list|)
operator|.
name|addNode
argument_list|(
name|Text
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|newNode
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
condition|)
block|{
name|NodeType
name|primaryType
init|=
name|newNode
operator|.
name|getPrimaryNodeType
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|newNode
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|,
name|primaryType
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|newNode
operator|.
name|getPrimaryNodeType
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Cannot read primary type from transient new node if access to property is not readable."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Unable to retrieve primary type for Node"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-2488">OAK-2488</a>      */
specifier|public
name|void
name|testGetMixinFromNewNode
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|addMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_LOCKABLE
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|deny
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|Node
name|newNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|testRoot
argument_list|)
operator|.
name|addNode
argument_list|(
name|Text
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|newNode
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
argument_list|)
expr_stmt|;
name|NodeType
index|[]
name|mixins
init|=
name|newNode
operator|.
name|getMixinNodeTypes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|mixins
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

