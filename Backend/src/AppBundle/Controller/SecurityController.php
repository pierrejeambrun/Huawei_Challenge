<?php

namespace AppBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Security\Core\Authentication\AuthenticationManagerInterface;
use Symfony\Component\Security\Core\Authentication\AuthenticationProviderManager;
use Symfony\Component\Security\Core\Authentication\Token\Storage\TokenStorageInterface;
use Symfony\Component\Security\Core\Authentication\Token\UsernamePasswordToken;
use Symfony\Component\Security\Core\Encoder\UserPasswordEncoder;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;
use Symfony\Component\Security\Core\Encoder\MessageDigestPasswordEncoder;

class SecurityController extends Controller
{
    /**
     * @var TokenStorageInterface
     */
    private $tokenStorage;

    /**
     * @var AuthenticationManagerInterface
     */
    private $authenticationManager;

    /**
     * @var string Uniquely identifies the secured area
     */
    private $providerKey = "this_provider";


    /**
     * @Route("/api/login", name="api_login_check")
     */
    public function loginAction(Request $request, AuthenticationUtils $authUtils)
    {
        // get the login error if there is one
        $error = $authUtils->getLastAuthenticationError();

        $username = $request->get('username');
        $password = $request->get('password');

        $encoder = new MessageDigestPasswordEncoder('sha512', true, 10);
        $user = $this->getDoctrine()->getRepository("AppBundle:User")->findBy([
            "username" => $username,
            "password" => $encoder->encodePassword($password, $username) //salt is username
        ]);

        return new JsonResponse(array("id" => $user->getId()));
        /*$unauthenticatedToken = new UsernamePasswordToken(
            $username,
            $password,
            $user
        );*/
        var_dump($encoder->encodePassword($password, $username));die;
        $authenticatedToken = $this
            ->authenticationManager
            ->authenticate($unauthenticatedToken);

        $this->tokenStorage->setToken($authenticatedToken);

        // instances of Symfony\Component\Security\Core\Authentication\Provider\AuthenticationProviderInterface


        try {
        $authenticatedToken = $authenticationManager
            ->authenticate($unauthenticatedToken);
        } catch (AuthenticationException $failed) {
        // authentication failed
        }

        // last username entered by the user
        $lastUsername = $authUtils->getLastUsername();

        return new JsonResponse(array('name' => $lastUsername, 'error' => $error));
    }

}