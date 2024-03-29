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
name|segment
operator|.
name|file
operator|.
name|tar
package|;
end_package

begin_class
specifier|public
class|class
name|TarConstants
block|{
specifier|private
name|TarConstants
parameter_list|()
block|{
comment|// Prevent instantiation.
block|}
specifier|static
specifier|final
name|String
name|FILE_NAME_FORMAT
init|=
literal|"data%05d%s.tar"
decl_stmt|;
comment|/**      * Magic byte sequence at the end of the graph block.      *<p>      * The file is read from the end (the tar file is read from the end: the      * last entry is the index, then the graph). File format:      *<ul>      *<li>0 padding to make the footer end at a 512 byte boundary</li>      *<li>The list of UUIDs (segments included the graph; this includes      * segments in this tar file, and referenced segments in tar files with a      * lower sequence number). 16 bytes each.</li>      *<li>The graph data. The index of the source segment UUID (in the above      * list, 4 bytes), then the list of referenced segments (the indexes of      * those; 4 bytes each). Then the list is terminated by -1.</li>      *<li>The last part is the footer, which contains metadata of the graph      * (size, checksum, the number of UUIDs).</li>      *</ul>      */
specifier|public
specifier|static
specifier|final
name|int
name|GRAPH_MAGIC
init|=
operator|(
literal|'\n'
operator|<<
literal|24
operator|)
operator|+
operator|(
literal|'0'
operator|<<
literal|16
operator|)
operator|+
operator|(
literal|'G'
operator|<<
literal|8
operator|)
operator|+
literal|'\n'
decl_stmt|;
comment|/**      * The tar file block size.      */
specifier|public
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|512
decl_stmt|;
block|}
end_class

end_unit

