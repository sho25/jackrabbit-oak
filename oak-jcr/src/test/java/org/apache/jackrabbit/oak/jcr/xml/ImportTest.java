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
name|xml
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ImportUUIDBehavior
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ItemExistsException
import|;
end_import

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
name|Property
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
name|ConstraintViolationException
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
name|AbstractJCRTest
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotEquals
import|;
end_import

begin_class
specifier|public
class|class
name|ImportTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|String
name|uuid
decl_stmt|;
specifier|private
name|String
name|path
decl_stmt|;
specifier|private
name|String
name|siblingPath
decl_stmt|;
annotation|@
name|Override
specifier|protected
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
name|Node
name|node
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|node
operator|.
name|addMixin
argument_list|(
name|mixReferenceable
argument_list|)
expr_stmt|;
name|Node
name|sibling
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|)
decl_stmt|;
name|uuid
operator|=
name|node
operator|.
name|getIdentifier
argument_list|()
expr_stmt|;
name|path
operator|=
name|node
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|siblingPath
operator|=
name|sibling
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
specifier|private
name|InputStream
name|getImportStream
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|OutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|superuser
operator|.
name|exportSystemView
argument_list|(
name|path
argument_list|,
name|out
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|testReplaceUUID
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|importXML
argument_list|(
name|siblingPath
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REPLACE_EXISTING
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// original node must have been replaced (but no child node added)
name|assertTrue
argument_list|(
name|testRootNode
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|testRootNode
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n2
operator|.
name|isNodeType
argument_list|(
name|mixReferenceable
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uuid
argument_list|,
name|n2
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|sibling
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|siblingPath
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|sibling
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-2246">OAK-2246</a>      */
specifier|public
name|void
name|testTransientReplaceUUID
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|importXML
argument_list|(
name|path
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REPLACE_EXISTING
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// original node must have been replaced (but no child node added)
name|superuser
operator|.
name|importXML
argument_list|(
name|siblingPath
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REPLACE_EXISTING
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// original node must have been replaced (but no child node added)
name|assertTrue
argument_list|(
name|testRootNode
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|testRootNode
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n2
operator|.
name|isNodeType
argument_list|(
name|mixReferenceable
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uuid
argument_list|,
name|n2
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|sibling
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|siblingPath
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|sibling
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testReplaceUUIDSameTree
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|importXML
argument_list|(
name|path
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REPLACE_EXISTING
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// original node must have been replaced (but no child node added)
name|assertTrue
argument_list|(
name|testRootNode
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|testRootNode
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n2
operator|.
name|isNodeType
argument_list|(
name|mixReferenceable
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uuid
argument_list|,
name|n2
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|n2
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-2246">OAK-2246</a>      */
specifier|public
name|void
name|testTransientReplaceUUIDSameTree
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|importXML
argument_list|(
name|path
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REPLACE_EXISTING
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// original node must have been replaced (but no child node added)
name|assertTrue
argument_list|(
name|testRootNode
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n2
init|=
name|testRootNode
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n2
operator|.
name|isNodeType
argument_list|(
name|mixReferenceable
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uuid
argument_list|,
name|n2
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|n2
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRemoveUUID
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|importXML
argument_list|(
name|siblingPath
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REMOVE_EXISTING
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// original node must have been removed
name|assertFalse
argument_list|(
name|testRootNode
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|sibling
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|siblingPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|sibling
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|imported
init|=
name|sibling
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|imported
operator|.
name|isNodeType
argument_list|(
name|mixReferenceable
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uuid
argument_list|,
name|imported
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTransientRemoveUUID
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|importXML
argument_list|(
name|siblingPath
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REMOVE_EXISTING
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// original node must have been removed
name|assertFalse
argument_list|(
name|testRootNode
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|sibling
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|siblingPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|sibling
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|imported
init|=
name|sibling
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|imported
operator|.
name|isNodeType
argument_list|(
name|mixReferenceable
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uuid
argument_list|,
name|imported
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRemoveUUIDSameTree
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|superuser
operator|.
name|importXML
argument_list|(
name|path
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REMOVE_EXISTING
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ConstraintViolationException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
specifier|public
name|void
name|testTransientRemoveUUIDSameTree
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|superuser
operator|.
name|importXML
argument_list|(
name|path
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REMOVE_EXISTING
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ConstraintViolationException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
specifier|public
name|void
name|testCreateNewUUID
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|importXML
argument_list|(
name|siblingPath
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_CREATE_NEW
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// original node must still exist
name|assertTrue
argument_list|(
name|testRootNode
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify the import produced the expected new node
name|Node
name|sibling
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|siblingPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|sibling
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|imported
init|=
name|sibling
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|imported
operator|.
name|isNodeType
argument_list|(
name|mixReferenceable
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|uuid
operator|.
name|equals
argument_list|(
name|imported
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTransientCreateNewUUID
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|importXML
argument_list|(
name|siblingPath
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_CREATE_NEW
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// original node must still exist
name|assertTrue
argument_list|(
name|testRootNode
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify the import produced the expected new node
name|Node
name|sibling
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|siblingPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|sibling
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|imported
init|=
name|sibling
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|imported
operator|.
name|isNodeType
argument_list|(
name|mixReferenceable
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|uuid
operator|.
name|equals
argument_list|(
name|imported
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testThrow
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|superuser
operator|.
name|importXML
argument_list|(
name|siblingPath
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_THROW
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ItemExistsException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ItemExistsException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
specifier|public
name|void
name|testTransientThrow
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|superuser
operator|.
name|importXML
argument_list|(
name|siblingPath
argument_list|,
name|getImportStream
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_THROW
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ItemExistsException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ItemExistsException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-8212">OAK-8212</a>      */
specifier|public
name|void
name|testNoMatchingPropertyDefinition
parameter_list|()
throws|throws
name|Exception
block|{
comment|// jcr:data must be BINARY not BOOLEAN -> should fail
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"resourceName\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>oak:Resource</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:data\" sv:type=\"Boolean\"><sv:value>true</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
try|try
block|{
name|superuser
operator|.
name|importXML
argument_list|(
name|path
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|xml
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_CREATE_NEW
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ConstraintViolationException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|"No matching property definition found for jcr:data"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testNewNamespaceWithPrefixConflict
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|ns1
init|=
literal|"urn:uuid:"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|ns2
init|=
literal|"urn:uuid:"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|xml
init|=
literal|"<sv:node sv:name='resourceName' xmlns:sv='http://www.jcp.org/jcr/sv/1.0'>"
operator|+
literal|"<sv:property xmlns:foo='"
operator|+
name|ns1
operator|+
literal|"' sv:name='foo:test' sv:type='String'><sv:value>a</sv:value></sv:property>"
operator|+
literal|"<sv:property xmlns:foo='"
operator|+
name|ns2
operator|+
literal|"' sv:name='foo:test' sv:type='String'><sv:value>b</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|superuser
operator|.
name|importXML
argument_list|(
name|path
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|xml
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_CREATE_NEW
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|n
operator|=
name|n
operator|.
name|getNode
argument_list|(
literal|"resourceName"
argument_list|)
expr_stmt|;
name|Property
name|p1
init|=
name|n
operator|.
name|getProperty
argument_list|(
literal|"{"
operator|+
name|ns1
operator|+
literal|"}test"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|p1
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|Property
name|p2
init|=
name|n
operator|.
name|getProperty
argument_list|(
literal|"{"
operator|+
name|ns2
operator|+
literal|"}test"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|p2
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|p1
operator|.
name|getName
argument_list|()
argument_list|,
name|p2
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

