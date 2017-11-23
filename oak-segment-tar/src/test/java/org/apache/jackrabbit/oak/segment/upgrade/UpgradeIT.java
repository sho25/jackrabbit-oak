begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
name|upgrade
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|StandardSystemProperty
operator|.
name|OS_NAME
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|transform
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|format
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MINUTES
import|;
end_import

begin_import
import|import static
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
name|SegmentVersion
operator|.
name|V_12
import|;
end_import

begin_import
import|import static
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
name|SegmentVersion
operator|.
name|V_13
import|;
end_import

begin_import
import|import static
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
name|data
operator|.
name|SegmentData
operator|.
name|newSegmentData
import|;
end_import

begin_import
import|import static
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
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
import|;
end_import

begin_import
import|import static
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
name|ManifestChecker
operator|.
name|newManifestChecker
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
name|assertEquals
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
name|assertTrue
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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|segment
operator|.
name|SegmentVersion
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
name|segment
operator|.
name|data
operator|.
name|SegmentData
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
name|segment
operator|.
name|file
operator|.
name|InvalidFileStoreVersionException
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|IOMonitorAdapter
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|TarFiles
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
name|segment
operator|.
name|tool
operator|.
name|Compact
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_class
specifier|public
class|class
name|UpgradeIT
block|{
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|fileStoreHome
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * Launch a groovy script in an Oak 1.6. console to initialise the upgrade      * source. See pom.xml for how these files are placed under target/upgrade-it.      */
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Process
name|oakConsole
init|=
operator|new
name|ProcessBuilder
argument_list|(
literal|"java"
argument_list|,
literal|"-jar"
argument_list|,
literal|"oak-run.jar"
argument_list|,
literal|"console"
argument_list|,
name|fileStoreHome
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"--read-write"
argument_list|,
literal|":load create16store.groovy"
argument_list|)
operator|.
name|directory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"upgrade-it"
argument_list|)
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Timeout while creating the source repository"
argument_list|,
name|oakConsole
operator|.
name|waitFor
argument_list|(
literal|2
argument_list|,
name|MINUTES
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|openUpgradesStore
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|checkStoreVersion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fileStoreBuilder
argument_list|(
name|fileStoreHome
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkStoreVersion
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|openReadonlyDoesNotUpgradeStore
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|checkStoreVersion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fileStoreBuilder
argument_list|(
name|fileStoreHome
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkStoreVersion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|offRCUpgradesSegments
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|checkSegmentVersion
argument_list|(
name|V_12
argument_list|)
expr_stmt|;
name|checkStoreVersion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Compact
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|fileStoreHome
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|withMmap
argument_list|(
literal|true
argument_list|)
operator|.
name|withOs
argument_list|(
name|OS_NAME
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|withForce
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// Upgraded
name|checkStoreVersion
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|checkSegmentVersion
argument_list|(
name|V_13
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|offRCUpgradesRequiresForce
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|checkSegmentVersion
argument_list|(
name|V_12
argument_list|)
expr_stmt|;
name|checkStoreVersion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Compact
operator|.
name|builder
argument_list|()
operator|.
name|withPath
argument_list|(
name|fileStoreHome
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|withMmap
argument_list|(
literal|true
argument_list|)
operator|.
name|withOs
argument_list|(
name|OS_NAME
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|withForce
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// Not upgraded
name|checkStoreVersion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|checkSegmentVersion
argument_list|(
name|V_12
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkStoreVersion
parameter_list|(
name|int
name|version
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|newManifestChecker
argument_list|(
operator|new
name|File
argument_list|(
name|fileStoreHome
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"manifest"
argument_list|)
argument_list|,
literal|true
argument_list|,
name|version
argument_list|,
name|version
argument_list|)
operator|.
name|checkManifest
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|checkSegmentVersion
parameter_list|(
annotation|@
name|Nonnull
name|SegmentVersion
name|version
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|TarFiles
name|tarFiles
init|=
name|TarFiles
operator|.
name|builder
argument_list|()
operator|.
name|withDirectory
argument_list|(
name|fileStoreHome
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|withTarRecovery
argument_list|(
parameter_list|(
name|_1
parameter_list|,
name|_2
parameter_list|,
name|_3
parameter_list|)
lambda|->
name|fail
argument_list|(
literal|"Unexpected recovery"
argument_list|)
argument_list|)
operator|.
name|withIOMonitor
argument_list|(
operator|new
name|IOMonitorAdapter
argument_list|()
argument_list|)
operator|.
name|withReadOnly
argument_list|()
operator|.
name|build
argument_list|()
init|)
block|{
for|for
control|(
name|SegmentData
name|segmentData
range|:
name|getSegments
argument_list|(
name|tarFiles
argument_list|)
control|)
block|{
name|SegmentVersion
name|actualVersion
init|=
name|SegmentVersion
operator|.
name|fromByte
argument_list|(
name|segmentData
operator|.
name|getVersion
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|format
argument_list|(
literal|"Segment version mismatch. Expected %s, found %s"
argument_list|,
name|version
argument_list|,
name|actualVersion
argument_list|)
argument_list|,
name|version
argument_list|,
name|actualVersion
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_function
specifier|private
specifier|static
name|Iterable
argument_list|<
name|SegmentData
argument_list|>
name|getSegments
parameter_list|(
annotation|@
name|Nonnull
name|TarFiles
name|tarFiles
parameter_list|)
block|{
return|return
name|transform
argument_list|(
name|tarFiles
operator|.
name|getSegmentIds
argument_list|()
argument_list|,
name|uuid
lambda|->
name|newSegmentData
argument_list|(
name|tarFiles
operator|.
name|readSegment
argument_list|(
name|uuid
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|uuid
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
end_function

unit|}
end_unit

