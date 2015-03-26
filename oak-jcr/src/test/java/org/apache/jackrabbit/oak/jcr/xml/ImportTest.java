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
name|OutputStream
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
name|Node
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

begin_class
specifier|public
class|class
name|ImportTest
extends|extends
name|AbstractJCRTest
block|{
specifier|public
name|void
name|testReplaceUUID
parameter_list|()
throws|throws
name|Exception
block|{
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
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
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
name|node
operator|.
name|getPath
argument_list|()
argument_list|,
name|out
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|importXML
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|,
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
comment|//  TODO : FIXME (OAK-2246)
comment|//    public void testTransientReplaceUUID() throws Exception {
comment|//        Node node = testRootNode.addNode(nodeName1);
comment|//        node.addMixin(mixReferenceable);
comment|//
comment|//        OutputStream out = new ByteArrayOutputStream();
comment|//        superuser.exportSystemView(node.getPath(), out, true, false);
comment|//
comment|//        superuser.importXML(node.getPath(), new ByteArrayInputStream(out.toString().getBytes()), ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
comment|//        superuser.save();
comment|//
comment|//        assertTrue(testRootNode.hasNode(nodeName1));
comment|//        Node n2 = testRootNode.getNode(nodeName1);
comment|//        assertTrue(n2.isNodeType(mixReferenceable));
comment|//        assertFalse(n2.hasNode(nodeName1));
comment|//    }
block|}
end_class

end_unit

