#import "ViewController.h"
@interface ViewController ()
@end
@implementation ViewController
-(id)init {
    if (self = [super init])  {
    }
    return self;
}

-(void)dealloc {
    [self tearDownGL];
    if (EAGLContext.currentContext == self.context) {
        [EAGLContext setCurrentContext:nil];
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    //printf("viewDidLoad\n");

    self.initialized = false;
    self.reshape = true;
    self.touches = [[NSArray alloc] init];
    self.gameWindow2 = [GameMainMyIosGameWindow2 myIosGameWindow2];
    self.rootGameMain = [GameMainRootGameMain rootGameMain];
    self.view.multipleTouchEnabled = YES;

    self.context = [[EAGLContext alloc] initWithAPI:(kEAGLRenderingAPIOpenGLES2)];
    if (self.context == nil) {
        printf("Failed to create ES context\n");
    }
    GLKView *view = (GLKView *)self.view;
    view.context = self.context;
    view.drawableDepthFormat = GLKViewDrawableDepthFormat24;
    [self setupGL];
}

-(void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    if (self.isViewLoaded && self.view.window != nil) {
        self.view = nil;
        [self tearDownGL];
        if (EAGLContext.currentContext == self.context) {
            [EAGLContext setCurrentContext:nil];
        }
        self.context = nil;
    }
}

-(void)setupGL {
    [EAGLContext setCurrentContext:self.context];
    NSString *path = NSBundle.mainBundle.resourcePath;
    if (path != nil) {
        NSString *rpath = [NSString stringWithFormat:@"%@%s", path, "/include/app/resources"];
        [NSFileManager.defaultManager changeCurrentDirectoryPath:rpath];
        [self.gameWindow2 setCustomCwdCwd:rpath];
    }
    [self engineInitialize];
    double width = self.view.frame.size.width;
    double height = self.view.frame.size.height;
    [self engineResize:width height:height];
}

-(void)tearDownGL {
    [EAGLContext setCurrentContext:nil];
    [self engineFinalize];
}

-(void)update {
    [self engineUpdate];
}

-(void)glkView:(GLKView *)view drawInRect:(CGRect)rect {
    if (!self.initialized) {
        self.initialized = true;
        [self.rootGameMain preRunMain];
        [self.gameWindow2.gameWindow dispatchInitEvent];
        [self.rootGameMain runMain];
        self.reshape = true;
    }
    double width = self.view.bounds.size.width * self.view.contentScaleFactor;
    double height = self.view.bounds.size.height * self.view.contentScaleFactor;
    if (self.reshape) {
        self.reshape = false;
        [_gameWindow2.gameWindow dispatchReshapeEventX:0 y:0 width:width height:height];
    }
    [_gameWindow2.gameWindow frame];
}

-(void) engineInitialize {
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    self.touches = [[NSArray alloc] init];
    [self.gameWindow2.gameWindow dispatchTouchEventModeIos];
    [self.gameWindow2.gameWindow dispatchTouchEventStartStart];
    [self addTouches:touches];
    [self.gameWindow2.gameWindow dispatchTouchEventEnd];
}

-(void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self.gameWindow2.gameWindow dispatchTouchEventModeIos];
    [self.gameWindow2.gameWindow dispatchTouchEventStartMove];
    [self addTouches:touches];
    [self.gameWindow2.gameWindow dispatchTouchEventEnd];
}

-(void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self.gameWindow2.gameWindow dispatchTouchEventModeIos];
    [self.gameWindow2.gameWindow dispatchTouchEventStartEnd];
    [self addTouches:touches];
    [self.gameWindow2.gameWindow dispatchTouchEventEnd];
}

-(void)addTouches:(NSSet<UITouch *> *)touches {
    for (UITouch* touch in touches) {
        CGPoint point = [touch locationInView:self.view];
        int index = -1;
        for (int n = 0; n < self.touches.count; n++) {
            if ([self.touches objectAtIndex:n] == touch) {
                index = n;
                break;
            }
        }
        if (index == -1) {
            index = (int)self.touches.count;
            self.touches = [self.touches arrayByAddingObject:touch];
        }
        [self.gameWindow2.gameWindow dispatchTouchEventAddTouchId:index x:point.x* self.view.contentScaleFactor y:point.y* self.view.contentScaleFactor];
    }
}

-(void)engineFinalize {
}

-(void)engineResize:(double)width height:(double)height {
}

-(void)engineUpdate {
}

@end
