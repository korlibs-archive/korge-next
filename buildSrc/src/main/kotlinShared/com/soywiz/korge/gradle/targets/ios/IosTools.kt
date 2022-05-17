package com.soywiz.korge.gradle.targets.ios

object IosTools {
    fun genBootstrapKt(entrypoint: String): String = """
        import $entrypoint
        
        @ThreadLocal
        object NewAppDelegate : com.soywiz.korgw.KorgwBaseNewAppDelegate() {
            override fun applicationDidFinishLaunching(app: platform.UIKit.UIApplication) { applicationDidFinishLaunching(app) { ${entrypoint}() } }
        }
    """.trimIndent()

    fun genMainObjC(): String = """
        #import <UIKit/UIKit.h>
        #import <GameMain/GameMain.h>

        @interface AppDelegate : UIResponder <UIApplicationDelegate>
        @property (strong, nonatomic) UIWindow *window;
        @end

        int main(int argc, char * argv[]) {
            @autoreleasepool { return UIApplicationMain(argc, argv, nil, NSStringFromClass([AppDelegate class])); }
        }

        @implementation AppDelegate
        - (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
            [[GameMainNewAppDelegate getNewAppDelegate] applicationDidFinishLaunchingApp: application];
            return YES;
        }
        - (void)applicationWillResignActive:(UIApplication *)application {
            [[GameMainNewAppDelegate getNewAppDelegate] applicationWillResignActiveApp: application];
        }
        - (void)applicationDidEnterBackground:(UIApplication *)application {
            [[GameMainNewAppDelegate getNewAppDelegate] applicationDidEnterBackgroundApp: application];
        }
        - (void)applicationWillEnterForeground:(UIApplication *)application {
            [[GameMainNewAppDelegate getNewAppDelegate] applicationWillEnterForegroundApp: application];
        }
        - (void)applicationDidBecomeActive:(UIApplication *)application {
            [[GameMainNewAppDelegate getNewAppDelegate] applicationDidBecomeActiveApp: application];
        }
        - (void)applicationWillTerminate:(UIApplication *)application {
            [[GameMainNewAppDelegate getNewAppDelegate] applicationWillTerminateApp: application];
        }
        @end
    """.trimIndent()

    fun genLaunchScreenStoryboard(): String = """
        <?xml version="1.0" encoding="UTF-8" standalone="no"?>
        <document type="com.apple.InterfaceBuilder3.CocoaTouch.Storyboard.XIB" version="3.0" toolsVersion="13122.16" targetRuntime="iOS.CocoaTouch" propertyAccessControl="none" useAutolayout="YES" launchScreen="YES" useTraitCollections="YES" useSafeAreas="YES" colorMatched="YES" initialViewController="01J-lp-oVM">
            <dependencies>
                <plugIn identifier="com.apple.InterfaceBuilder.IBCocoaTouchPlugin" version="13104.12"/>
                <capability name="Safe area layout guides" minToolsVersion="9.0"/>
                <capability name="documents saved in the Xcode 8 format" minToolsVersion="8.0"/>
            </dependencies>
            <scenes>
                <!--View Controller-->
                <scene sceneID="EHf-IW-A2E">
                    <objects>
                        <viewController id="01J-lp-oVM" sceneMemberID="viewController">
                            <view key="view" contentMode="scaleToFill" id="Ze5-6b-2t3">
                                <rect key="frame" x="0.0" y="0.0" width="375" height="667"/>
                                <autoresizingMask key="autoresizingMask" widthSizable="YES" heightSizable="YES"/>
                                <color key="backgroundColor" red="1" green="1" blue="1" alpha="1" colorSpace="custom" customColorSpace="sRGB"/>
                                <viewLayoutGuide key="safeArea" id="6Tk-OE-BBY"/>
                            </view>
                        </viewController>
                        <placeholder placeholderIdentifier="IBFirstResponder" id="iYj-Kq-Ea1" userLabel="First Responder" sceneMemberID="firstResponder"/>
                    </objects>
                    <point key="canvasLocation" x="53" y="375"/>
                </scene>
            </scenes>
        </document>
    """.trimIndent()
}
