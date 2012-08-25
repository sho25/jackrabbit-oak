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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|PathNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
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
name|NodeDefinitionTemplate
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
name|NodeTypeManager
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
name|NodeTypeTemplate
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
name|PropertyDefinitionTemplate
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|OnParentVersionAction
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
name|commons
operator|.
name|JcrUtils
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
name|value
operator|.
name|BinaryValue
import|;
end_import

begin_class
specifier|public
class|class
name|TestContentLoader
block|{
comment|/**      * The encoding of the test resources.      */
specifier|private
specifier|static
specifier|final
name|String
name|ENCODING
init|=
literal|"UTF-8"
decl_stmt|;
specifier|public
name|void
name|loadTestContent
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getNamespaceRegistry
argument_list|()
operator|.
name|registerNamespace
argument_list|(
literal|"test"
argument_list|,
literal|"http://www.apache.org/jackrabbit/test"
argument_list|)
expr_stmt|;
name|NodeTypeManager
name|ntm
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
comment|// TEST NODE TYPES (in Jackrabbit-core: imported from XML file using JackrabbitNodeTypeManager)
comment|// test:setProperty
block|{
name|NodeTypeTemplate
name|nttmpl
init|=
name|ntm
operator|.
name|createNodeTypeTemplate
argument_list|()
decl_stmt|;
name|nttmpl
operator|.
name|setName
argument_list|(
literal|"test:setProperty"
argument_list|)
expr_stmt|;
name|nttmpl
operator|.
name|setDeclaredSuperTypeNames
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"nt:base"
block|,
literal|"mix:referenceable"
block|}
argument_list|)
expr_stmt|;
name|NodeDefinitionTemplate
name|ndtmpl
init|=
name|ntm
operator|.
name|createNodeDefinitionTemplate
argument_list|()
decl_stmt|;
name|ndtmpl
operator|.
name|setName
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|ndtmpl
operator|.
name|setRequiredPrimaryTypeNames
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test:setProperty"
block|}
argument_list|)
expr_stmt|;
name|ndtmpl
operator|.
name|setOnParentVersion
argument_list|(
name|OnParentVersionAction
operator|.
name|COPY
argument_list|)
expr_stmt|;
name|nttmpl
operator|.
name|getNodeDefinitionTemplates
argument_list|()
operator|.
name|add
argument_list|(
name|ndtmpl
argument_list|)
expr_stmt|;
name|PropertyDefinitionTemplate
name|pdtmpl
init|=
name|ntm
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
decl_stmt|;
name|pdtmpl
operator|.
name|setName
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setOnParentVersion
argument_list|(
name|OnParentVersionAction
operator|.
name|COPY
argument_list|)
expr_stmt|;
name|nttmpl
operator|.
name|getPropertyDefinitionTemplates
argument_list|()
operator|.
name|add
argument_list|(
name|pdtmpl
argument_list|)
expr_stmt|;
name|ntm
operator|.
name|registerNodeType
argument_list|(
name|nttmpl
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// test:canSetProperty
comment|// TODO: add all property definitions from jackrabbit-core/src/main/resources/org/apache/jackrabbit/core/test-nodetypes.xml
block|{
name|NodeTypeTemplate
name|nttmpl
init|=
name|ntm
operator|.
name|createNodeTypeTemplate
argument_list|()
decl_stmt|;
name|nttmpl
operator|.
name|setName
argument_list|(
literal|"test:canSetProperty"
argument_list|)
expr_stmt|;
name|nttmpl
operator|.
name|setDeclaredSuperTypeNames
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"nt:base"
block|}
argument_list|)
expr_stmt|;
comment|// add property definitions
name|PropertyDefinitionTemplate
name|pdtmpl
init|=
name|ntm
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
decl_stmt|;
name|pdtmpl
operator|.
name|setName
argument_list|(
literal|"String"
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setRequiredType
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setOnParentVersion
argument_list|(
name|OnParentVersionAction
operator|.
name|COPY
argument_list|)
expr_stmt|;
name|nttmpl
operator|.
name|getPropertyDefinitionTemplates
argument_list|()
operator|.
name|add
argument_list|(
name|pdtmpl
argument_list|)
expr_stmt|;
name|pdtmpl
operator|=
name|ntm
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
expr_stmt|;
name|pdtmpl
operator|.
name|setName
argument_list|(
literal|"StringConstraints"
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setRequiredType
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setOnParentVersion
argument_list|(
name|OnParentVersionAction
operator|.
name|COPY
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setValueConstraints
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"abc"
block|,
literal|"def"
block|,
literal|"ghi"
block|}
argument_list|)
expr_stmt|;
name|nttmpl
operator|.
name|getPropertyDefinitionTemplates
argument_list|()
operator|.
name|add
argument_list|(
name|pdtmpl
argument_list|)
expr_stmt|;
name|pdtmpl
operator|=
name|ntm
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
expr_stmt|;
name|pdtmpl
operator|.
name|setName
argument_list|(
literal|"StringMultipleConstraints"
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setMultiple
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setRequiredType
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setOnParentVersion
argument_list|(
name|OnParentVersionAction
operator|.
name|COPY
argument_list|)
expr_stmt|;
name|nttmpl
operator|.
name|getPropertyDefinitionTemplates
argument_list|()
operator|.
name|add
argument_list|(
name|pdtmpl
argument_list|)
expr_stmt|;
name|pdtmpl
operator|=
name|ntm
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
expr_stmt|;
name|pdtmpl
operator|.
name|setName
argument_list|(
literal|"Binary"
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setRequiredType
argument_list|(
name|PropertyType
operator|.
name|BINARY
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setOnParentVersion
argument_list|(
name|OnParentVersionAction
operator|.
name|COPY
argument_list|)
expr_stmt|;
name|nttmpl
operator|.
name|getPropertyDefinitionTemplates
argument_list|()
operator|.
name|add
argument_list|(
name|pdtmpl
argument_list|)
expr_stmt|;
name|pdtmpl
operator|=
name|ntm
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
expr_stmt|;
name|pdtmpl
operator|.
name|setName
argument_list|(
literal|"BinaryConstraints"
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setRequiredType
argument_list|(
name|PropertyType
operator|.
name|BINARY
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setOnParentVersion
argument_list|(
name|OnParentVersionAction
operator|.
name|COPY
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setValueConstraints
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(,100)"
block|}
argument_list|)
expr_stmt|;
name|nttmpl
operator|.
name|getPropertyDefinitionTemplates
argument_list|()
operator|.
name|add
argument_list|(
name|pdtmpl
argument_list|)
expr_stmt|;
name|pdtmpl
operator|=
name|ntm
operator|.
name|createPropertyDefinitionTemplate
argument_list|()
expr_stmt|;
name|pdtmpl
operator|.
name|setName
argument_list|(
literal|"BinaryMultipleConstraints"
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setMultiple
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setRequiredType
argument_list|(
name|PropertyType
operator|.
name|BINARY
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setOnParentVersion
argument_list|(
name|OnParentVersionAction
operator|.
name|COPY
argument_list|)
expr_stmt|;
name|pdtmpl
operator|.
name|setValueConstraints
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"(,100)"
block|}
argument_list|)
expr_stmt|;
name|nttmpl
operator|.
name|getPropertyDefinitionTemplates
argument_list|()
operator|.
name|add
argument_list|(
name|pdtmpl
argument_list|)
expr_stmt|;
name|ntm
operator|.
name|registerNodeType
argument_list|(
name|nttmpl
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|Node
name|data
init|=
name|getOrAddNode
argument_list|(
name|session
operator|.
name|getRootNode
argument_list|()
argument_list|,
literal|"testdata"
argument_list|)
decl_stmt|;
name|addPropertyTestData
argument_list|(
name|getOrAddNode
argument_list|(
name|data
argument_list|,
literal|"property"
argument_list|)
argument_list|)
expr_stmt|;
name|addQueryTestData
argument_list|(
name|getOrAddNode
argument_list|(
name|data
argument_list|,
literal|"query"
argument_list|)
argument_list|)
expr_stmt|;
name|addNodeTestData
argument_list|(
name|getOrAddNode
argument_list|(
name|data
argument_list|,
literal|"node"
argument_list|)
argument_list|)
expr_stmt|;
name|addExportTestData
argument_list|(
name|getOrAddNode
argument_list|(
name|data
argument_list|,
literal|"docViewTest"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Node
name|getOrAddNode
parameter_list|(
name|Node
name|node
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
return|return
name|node
operator|.
name|getNode
argument_list|(
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
return|return
name|node
operator|.
name|addNode
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
comment|/**      * Creates a boolean, double, long, calendar and a path property at the      * given node.      */
specifier|private
name|void
name|addPropertyTestData
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|node
operator|.
name|setProperty
argument_list|(
literal|"boolean"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"double"
argument_list|,
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"long"
argument_list|,
literal|90834953485278298l
argument_list|)
expr_stmt|;
name|Calendar
name|c
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|c
operator|.
name|set
argument_list|(
literal|2005
argument_list|,
literal|6
argument_list|,
literal|18
argument_list|,
literal|17
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"calendar"
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|ValueFactory
name|factory
init|=
name|node
operator|.
name|getSession
argument_list|()
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"path"
argument_list|,
name|factory
operator|.
name|createValue
argument_list|(
literal|"/"
argument_list|,
name|PropertyType
operator|.
name|PATH
argument_list|)
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"multi"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"one"
block|,
literal|"two"
block|,
literal|"three"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates four nodes under the given node. Each node has a String property      * named "prop1" with some content set.      */
specifier|private
name|void
name|addQueryTestData
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
while|while
condition|(
name|node
operator|.
name|hasNode
argument_list|(
literal|"node1"
argument_list|)
condition|)
block|{
name|node
operator|.
name|getNode
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|getOrAddNode
argument_list|(
name|node
argument_list|,
literal|"node1"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop1"
argument_list|,
literal|"You can have it good, cheap, or fast. Any two."
argument_list|)
expr_stmt|;
name|getOrAddNode
argument_list|(
name|node
argument_list|,
literal|"node1"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop1"
argument_list|,
literal|"foo bar"
argument_list|)
expr_stmt|;
name|getOrAddNode
argument_list|(
name|node
argument_list|,
literal|"node1"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop1"
argument_list|,
literal|"Hello world!"
argument_list|)
expr_stmt|;
name|getOrAddNode
argument_list|(
name|node
argument_list|,
literal|"node2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop1"
argument_list|,
literal|"Apache Jackrabbit"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates three nodes under the given node: one of type nt:resource and the      * other nodes referencing it.      */
specifier|private
name|void
name|addNodeTestData
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
if|if
condition|(
name|node
operator|.
name|hasNode
argument_list|(
literal|"multiReference"
argument_list|)
condition|)
block|{
name|node
operator|.
name|getNode
argument_list|(
literal|"multiReference"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|.
name|hasNode
argument_list|(
literal|"resReference"
argument_list|)
condition|)
block|{
name|node
operator|.
name|getNode
argument_list|(
literal|"resReference"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|.
name|hasNode
argument_list|(
literal|"myResource"
argument_list|)
condition|)
block|{
name|node
operator|.
name|getNode
argument_list|(
literal|"myResource"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|Node
name|resource
init|=
name|node
operator|.
name|addNode
argument_list|(
literal|"myResource"
argument_list|,
literal|"nt:resource"
argument_list|)
decl_stmt|;
comment|// nt:resource not longer referenceable since JCR 2.0
name|resource
operator|.
name|addMixin
argument_list|(
literal|"mix:referenceable"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
literal|"jcr:encoding"
argument_list|,
name|ENCODING
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
literal|"jcr:mimeType"
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
literal|"jcr:data"
argument_list|,
operator|new
name|BinaryValue
argument_list|(
literal|"Hello w\u00F6rld."
operator|.
name|getBytes
argument_list|(
name|ENCODING
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
literal|"jcr:lastModified"
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: re-add once we have referenceable nodes
comment|// Node resReference = getOrAddNode(node, "reference");
comment|// resReference.setProperty("ref", resource);
comment|// // make this node itself referenceable
comment|// resReference.addMixin("mix:referenceable");
comment|//
comment|// Node multiReference = node.addNode("multiReference");
comment|// ValueFactory factory = node.getSession().getValueFactory();
comment|// multiReference.setProperty("ref", new Value[] {
comment|// factory.createValue(resource),
comment|// factory.createValue(resReference)
comment|// });
comment|// NodeDefTest requires a test node with a mandatory child node
name|JcrUtils
operator|.
name|putFile
argument_list|(
name|node
argument_list|,
literal|"testFile"
argument_list|,
literal|"text/plain"
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"Hello, World!"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addExportTestData
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|getOrAddNode
argument_list|(
name|node
argument_list|,
literal|"invalidXmlName"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propName"
argument_list|,
literal|"some text"
argument_list|)
expr_stmt|;
comment|// three nodes which should be serialized as xml text in docView export
comment|// separated with spaces
name|getOrAddNode
argument_list|(
name|node
argument_list|,
literal|"jcr:xmltext"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:xmlcharacters"
argument_list|,
literal|"A text without any special character."
argument_list|)
expr_stmt|;
name|getOrAddNode
argument_list|(
name|node
argument_list|,
literal|"some-element"
argument_list|)
expr_stmt|;
name|getOrAddNode
argument_list|(
name|node
argument_list|,
literal|"jcr:xmltext"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:xmlcharacters"
argument_list|,
literal|" The entity reference characters:<, ', ,&,>,  \" should"
operator|+
literal|" be escaped in xml export. "
argument_list|)
expr_stmt|;
name|getOrAddNode
argument_list|(
name|node
argument_list|,
literal|"some-element"
argument_list|)
expr_stmt|;
name|getOrAddNode
argument_list|(
name|node
argument_list|,
literal|"jcr:xmltext"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:xmlcharacters"
argument_list|,
literal|"A text without any special character."
argument_list|)
expr_stmt|;
name|Node
name|big
init|=
name|getOrAddNode
argument_list|(
name|node
argument_list|,
literal|"bigNode"
argument_list|)
decl_stmt|;
name|big
operator|.
name|setProperty
argument_list|(
literal|"propName0"
argument_list|,
literal|"SGVsbG8gd8O2cmxkLg==;SGVsbG8gd8O2cmxkLg=="
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
argument_list|,
name|PropertyType
operator|.
name|BINARY
argument_list|)
expr_stmt|;
name|big
operator|.
name|setProperty
argument_list|(
literal|"propName1"
argument_list|,
literal|"text 1"
argument_list|)
expr_stmt|;
name|big
operator|.
name|setProperty
argument_list|(
literal|"propName2"
argument_list|,
literal|"multival text 1;multival text 2;multival text 3"
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
argument_list|)
expr_stmt|;
name|big
operator|.
name|setProperty
argument_list|(
literal|"propName3"
argument_list|,
literal|"text 1"
argument_list|)
expr_stmt|;
name|addExportValues
argument_list|(
name|node
argument_list|,
literal|"propName"
argument_list|)
expr_stmt|;
name|addExportValues
argument_list|(
name|node
argument_list|,
literal|"Prop<>prop"
argument_list|)
expr_stmt|;
block|}
comment|/**      * create nodes with following properties binary& single binary& multival      * notbinary& single notbinary& multival      */
specifier|private
name|void
name|addExportValues
parameter_list|(
name|Node
name|node
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|String
name|prefix
init|=
literal|"valid"
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'<'
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|prefix
operator|=
literal|"invalid"
expr_stmt|;
block|}
name|node
operator|=
name|getOrAddNode
argument_list|(
name|node
argument_list|,
name|prefix
operator|+
literal|"Names"
argument_list|)
expr_stmt|;
name|String
index|[]
name|texts
init|=
operator|new
name|String
index|[]
block|{
literal|"multival text 1"
block|,
literal|"multival text 2"
block|,
literal|"multival text 3"
block|}
decl_stmt|;
name|getOrAddNode
argument_list|(
name|node
argument_list|,
name|prefix
operator|+
literal|"MultiNoBin"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|texts
argument_list|)
expr_stmt|;
name|Node
name|resource
init|=
name|getOrAddNode
argument_list|(
name|node
argument_list|,
name|prefix
operator|+
literal|"MultiBin"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
literal|"jcr:encoding"
argument_list|,
name|ENCODING
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
literal|"jcr:mimeType"
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[]
block|{
literal|"SGVsbG8gd8O2cmxkLg=="
block|,
literal|"SGVsbG8gd8O2cmxkLg=="
block|}
decl_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|values
argument_list|,
name|PropertyType
operator|.
name|BINARY
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
literal|"jcr:lastModified"
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|getOrAddNode
argument_list|(
name|node
argument_list|,
name|prefix
operator|+
literal|"NoBin"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
literal|"text 1"
argument_list|)
expr_stmt|;
name|resource
operator|=
name|getOrAddNode
argument_list|(
name|node
argument_list|,
literal|"invalidBin"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
literal|"jcr:encoding"
argument_list|,
name|ENCODING
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
literal|"jcr:mimeType"
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
literal|"Hello w\u00F6rld."
operator|.
name|getBytes
argument_list|(
name|ENCODING
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
operator|new
name|BinaryValue
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
literal|"jcr:lastModified"
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

